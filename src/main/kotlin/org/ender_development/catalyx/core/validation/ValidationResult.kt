package org.ender_development.catalyx.core.validation

import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationResult

class ValidationResult<T> private constructor(override val data: T?, override val errors: List<IValidationError>) : IValidationResult<T> {
	override val success = errors.isEmpty()
	override val failure = !success
	override val errorMessages = errors.map(IValidationError::message)

	companion object {
		fun <T> success(data: T) =
			ValidationResult(data, emptyList())

		fun <T> failure(errors: List<IValidationError>) =
			ValidationResult<T>(null, errors)
	}
}
