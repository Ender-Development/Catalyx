package org.ender_development.catalyx.core.validation

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError

@Suppress("UNUSED")
data class ValidationError(
	override val field: String? = null,
	override val message: String,
	override val code: String? = null,
	override val severity: Severity = Severity.ERROR
) : IValidationError {
	override fun toString(): String {
		val prefix = when(severity) {
			Severity.WARNING -> "⚠️"
			Severity.ERROR -> "❌"
			Severity.CRITICAL -> "🚨"
		}
		return field?.let { "$prefix [$field]: $message" } ?: "$prefix: $message"
	}

	// TODO Add to IValidationError if publicity is needed
	fun log(logger: Logger = Catalyx.LOGGER) =
		logger.log(severity.loggerLevel, toString())
}
