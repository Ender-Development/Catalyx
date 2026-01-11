package org.ender_development.catalyx.utils.validation

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx_.core.Catalyx

@Suppress("UNUSED")
data class ValidationError(val field: String? = null, val message: String, val code: String? = null, val severity: Severity = Severity.ERROR) {
	enum class Severity(val loggerLevel: Level) {
		WARNING(Level.WARN),
		ERROR(Level.ERROR),
		CRITICAL(Level.FATAL)
	}

	override fun toString(): String {
		val prefix = when(severity) {
			Severity.WARNING -> "⚠️"
			Severity.ERROR -> "❌"
			Severity.CRITICAL -> "🚨"
		}
		return field?.let { "$prefix [$field]: $message" } ?: "$prefix: $message"
	}

	fun log(logger: Logger = Catalyx.LOGGER) =
		logger.log(severity.loggerLevel, toString())
}
