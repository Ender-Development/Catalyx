package org.ender_development.catalyx.utils.parser

import org.ender_development.catalyx.utils.validation.ValidationError

data class ParsingStats(
	val totalItems: Int = 0,
	val successfulItems: Int = 0,
	val failedItems: Int = 0,
	val errors: List<ValidationError> = emptyList(),
	val warnings: List<ValidationError> = emptyList()
) {
	fun hasErrors() =
		errors.isNotEmpty()

	fun hasWarnings() =
		warnings.isNotEmpty()

	fun getSuccessRate() =
		if (totalItems == 0) 0.0 else successfulItems.toDouble() / totalItems

	fun getErrorMessages() =
		errors.map { it.message }

	fun getWarningMessages() =
		warnings.map { it.message }
}
