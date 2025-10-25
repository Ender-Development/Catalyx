package org.ender_development.catalyx.utils.parser

import com.cleanroommc.groovyscript.helper.JsonHelper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.utils.validation.ValidationError
import org.ender_development.catalyx.utils.validation.ValidationResult
import org.ender_development.catalyx.utils.validation.getByMinSeverity
import org.ender_development.catalyx.utils.validation.getBySeverity
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class AbstractJsonParser<TRaw, TSanitized> : IParser<TSanitized> {
	private val gson = GsonBuilder().setPrettyPrinting().create()
	private var lastParsingStats = ParsingStats()

	abstract fun getDefaultRawData(): List<TRaw>
	abstract fun sanitize(rawData: TRaw): ValidationResult<TSanitized>
	abstract fun getRawTypeToken(): TypeToken<List<TRaw>>

	override fun parse(): List<TSanitized> {
		val file = File(getFilePath())

		if(!file.exists())
			createDefaultFile(file)

		val rawData = try {
			FileReader(file).use { gson.fromJson<List<TRaw>>(it, getRawTypeToken().type) }
		} catch(e: Exception) {
			Catalyx.LOGGER.error("Error reading JSON file: ${e.message}")
			getDefaultRawData()
		}

		val results = rawData?.map { sanitize(it) } ?: emptyList()
		val successfulItems = mutableListOf<TSanitized>()
		val allErrors = mutableListOf<ValidationError>()
		val allWarnings = mutableListOf<ValidationError>()

		results.forEachIndexed { idx, result ->
			when(result) {
				is ValidationResult.Success -> successfulItems.add(result.data)
				is ValidationResult.Failure -> {
					val errors = result.errors.getByMinSeverity(ValidationError.Severity.ERROR)
					val warnings = result.errors.getBySeverity(ValidationError.Severity.WARNING)

					val contextualErrors = errors.map { it.copy(message = "Item #$idx: ${it.message}") }
					val contextualWarnings = warnings.map { it.copy(message = "Item #$idx: ${it.message}") }
					allErrors.addAll(contextualErrors)
					allWarnings.addAll(contextualWarnings)
					logValidationIssues(idx, contextualErrors, contextualWarnings)
				}
			}
		}

		lastParsingStats = ParsingStats(
            totalItems = results.size,
            successfulItems = successfulItems.size,
            failedItems = results.size - successfulItems.size,
            errors = allErrors,
            warnings = allWarnings
        )

		logParsingSummary()

		return successfulItems
	}

	override fun getStats(): ParsingStats =
		lastParsingStats

	private fun logValidationIssues(itemIndex: Int, errors: List<ValidationError>, warnings: List<ValidationError>) {
		if(errors.isNotEmpty()) {
			Catalyx.LOGGER.error("❌ Failed to parse item $itemIndex from ${getFilePath()}:")
			errors.forEach { Catalyx.LOGGER.error("   $it") }
		}

		if(warnings.isNotEmpty()) {
			Catalyx.LOGGER.warn("⚠️ Warnings for item $itemIndex from ${getFilePath()}:")
			warnings.forEach { Catalyx.LOGGER.warn("   $it") }
		}
	}

	private fun logParsingSummary() {
		val stats = lastParsingStats
		val successRate = (stats.getSuccessRate() * 100).let { "%.1f".format(it) }

		Catalyx.LOGGER.info("📊 Parsing Summary for ${getFilePath()}:")
		Catalyx.LOGGER.info("   Total items: ${stats.totalItems}")
		Catalyx.LOGGER.info("   ✅ Successful: ${stats.successfulItems}")
		Catalyx.LOGGER.info("   ❌ Failed: ${stats.failedItems}")
		Catalyx.LOGGER.info("   📈 Success rate: $successRate%")

		if(stats.hasErrors()) {
			Catalyx.LOGGER.info("   🔍 Validation errors: ${stats.errors.size}")
			val criticalErrors = stats.errors.getBySeverity(ValidationError.Severity.CRITICAL)
			val regularErrors = stats.errors.getBySeverity(ValidationError.Severity.ERROR)

			if(criticalErrors.isNotEmpty()) {
				Catalyx.LOGGER.info("     🚨 Critical: ${criticalErrors.size}")
			}
			if(regularErrors.isNotEmpty()) {
				Catalyx.LOGGER.info("     ❌ Regular: ${regularErrors.size}")
			}
		}

		if(stats.hasWarnings()) {
			Catalyx.LOGGER.info("   ⚠️ Warnings: ${stats.warnings.size}")
		}
	}

	private fun createDefaultFile(file: File) =
		try {
			file.parentFile?.mkdirs()
			FileWriter(file).use { writer -> JsonHelper.gson.toJson(getDefaultRawData(), writer) }
			Catalyx.LOGGER.info("Created default JSON file: ${file.absolutePath}")
		} catch(e: Exception) {
			Catalyx.LOGGER.error("Error creating default file: ${e.message}")
		}
}
