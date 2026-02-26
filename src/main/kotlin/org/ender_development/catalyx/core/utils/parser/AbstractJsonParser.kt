package org.ender_development.catalyx.core.utils.parser

import com.cleanroommc.groovyscript.helper.JsonHelper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.common.extensions.getByMinSeverity
import org.ender_development.catalyx.api.v1.common.extensions.getBySeverity
import org.ender_development.catalyx.api.v1.validation.Validation.newValidationError
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError
import org.ender_development.catalyx.core.validation.ValidationResult
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class AbstractJsonParser<TRaw, TSanitized> : IParser<TSanitized> {
	private val gson = GsonBuilder().setPrettyPrinting().create()
	private var lastParsingStats = ParsingStats()

	abstract val defaultRawData: List<TRaw>
	abstract val rawTypeToken: TypeToken<List<TRaw>>
	abstract fun sanitize(rawData: TRaw): ValidationResult<TSanitized>

	override fun parse(): List<TSanitized> {
		val file = File(input)

		if(!file.exists())
			createDefaultFile(file)

		val rawData = try {
			FileReader(file).use { gson.fromJson(it, rawTypeToken.type) }
		} catch(e: Exception) {
			Catalyx.LOGGER.error("Error reading JSON file", e)
			defaultRawData
		}

		val results = rawData.map(::sanitize)
		val successfulItems = mutableListOf<TSanitized>()
		val allErrors = mutableListOf<IValidationError>()
		val allWarnings = mutableListOf<IValidationError>()

		results.forEachIndexed { idx, result ->
			when {
				result.success -> successfulItems.add(result.data!!)
				result.failure -> {
					val errors = result.errors.getByMinSeverity(Severity.ERROR)
					val warnings = result.errors.getBySeverity(Severity.WARNING)

					val contextualErrors = errors.map {
						newValidationError(it.field, "Item #$idx: ${it.message}", it.code, it.severity)
					}
					val contextualWarnings = warnings.map {
						newValidationError(it.field, "Item #$idx: ${it.message}", it.code, it.severity)
					}
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

	override val stats: ParsingStats
		get() = lastParsingStats

	private fun logValidationIssues(itemIndex: Int, errors: List<IValidationError>, warnings: List<IValidationError>) {
		if(errors.isNotEmpty()) {
			Catalyx.LOGGER.error("❌ Failed to parse item $itemIndex from $input:")
			errors.forEach { Catalyx.LOGGER.error("   $it") }
		}

		if(warnings.isNotEmpty()) {
			Catalyx.LOGGER.warn("⚠️ Warnings for item $itemIndex from $input:")
			warnings.forEach { Catalyx.LOGGER.warn("   $it") }
		}
	}

	private fun logParsingSummary() {
		val stats = lastParsingStats
		val successRate = stats.successRate * 100

		Catalyx.LOGGER.info("📊 Parsing Summary for $input:")
		Catalyx.LOGGER.info("   Total items: ${stats.totalItems}")
		Catalyx.LOGGER.info("   ✅ Successful: ${stats.successfulItems}")
		Catalyx.LOGGER.info("   ❌ Failed: ${stats.failedItems}")
		Catalyx.LOGGER.info("   📈 Success rate: %.1f%%".format(successRate))

		if(stats.hasErrors) {
			Catalyx.LOGGER.info("   🔍 Validation errors: ${stats.errors.size}")
			val criticalErrors = stats.errors.getBySeverity(Severity.CRITICAL)
			val regularErrors = stats.errors.getBySeverity(Severity.ERROR)

			if(criticalErrors.isNotEmpty())
				Catalyx.LOGGER.info("     🚨 Critical: ${criticalErrors.size}")
			if(regularErrors.isNotEmpty())
				Catalyx.LOGGER.info("     ❌ Regular: ${regularErrors.size}")
		}

		if(stats.hasWarnings)
			Catalyx.LOGGER.info("   ⚠️ Warnings: ${stats.warnings.size}")
	}

	private fun createDefaultFile(file: File) =
		try {
			file.parentFile?.mkdirs()
			FileWriter(file).use { writer -> JsonHelper.gson.toJson(defaultRawData, writer) }
			Catalyx.LOGGER.info("Created default JSON file: ${file.absolutePath}")
		} catch(e: Exception) {
			Catalyx.LOGGER.error("Error creating default file: ${e.message}")
		}
}
