package org.ender_development.catalyx.core.utils.parser

import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError

data class ParsingStats(
	val totalItems: Int = 0,
	val successfulItems: Int = 0,
	val failedItems: Int = 0,
	val errors: List<IValidationError> = emptyList(),
	val warnings: List<IValidationError> = emptyList()
) {
	val hasErrors = errors.isNotEmpty()
	val hasWarnings = warnings.isNotEmpty()

	val successRate = if(totalItems == 0) .0 else successfulItems.toDouble() / totalItems

	val errorMessages = errors.map(IValidationError::message)
	val warningMessages = warnings.map(IValidationError::message)
}
