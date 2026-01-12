package org.ender_development.catalyx.core.utils.parser

import org.ender_development.catalyx.core.utils.validation.ValidationError

data class ParsingStats(
	val totalItems: Int = 0,
	val successfulItems: Int = 0,
	val failedItems: Int = 0,
	val errors: List<ValidationError> = emptyList(),
	val warnings: List<ValidationError> = emptyList()
) {
	val hasErrors = errors.isNotEmpty()
	val hasWarnings = warnings.isNotEmpty()

	val successRate = if(totalItems == 0) .0 else successfulItems.toDouble() / totalItems

	val errorMessages = errors.map(ValidationError::message)
	val warningMessages = warnings.map(ValidationError::message)
}
