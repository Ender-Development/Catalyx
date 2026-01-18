package org.ender_development.catalyx.core.validation

import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationResult

@Suppress("UNUSED")
class ValidationResult<T> private constructor(
	override val data: T?,
	override val errors: List<IValidationError>
) : IValidationResult<T> {
	override val success: Boolean = errors.isEmpty()
	override val failure: Boolean = !success
	override val errorMessages: List<String> = errors.map(IValidationError::message)

	companion object {
		fun <T> success(data: T): ValidationResult<T> =
			ValidationResult(data, emptyList())

		fun failure(errors: List<IValidationError>) =
			ValidationResult(null, errors)
	}
}
