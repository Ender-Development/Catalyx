package org.ender_development.catalyx.core.validation

import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError

data class ValidationError(
	override val field: String? = null,
	override val message: String,
	override val code: String? = null,
	override val severity: Severity = Severity.ERROR
) : IValidationError {
	override fun toString() = buildString {
		append(severity.emoji)
		if(field != null)
			append(" [$field]")
		append(": $message")
	}

	@Suppress("NOTHING_TO_INLINE")
	internal inline fun log() =
		log(Catalyx.LOGGER)
}
