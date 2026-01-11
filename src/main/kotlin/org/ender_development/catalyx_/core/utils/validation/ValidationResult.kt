package org.ender_development.catalyx_.core.utils.validation

import kotlin.collections.map

@Suppress("UNUSED")
class ValidationResult<T> private constructor(
	val data: T?,
	val errors: List<ValidationError>
) {
	val success: Boolean = errors.isEmpty()
	val failure: Boolean = !success
	val errorMessages: List<String> = errors.map(ValidationError::message)

	companion object {
		fun <T> success(data: T): ValidationResult<T> =
			ValidationResult(data, emptyList())

		fun failure(errors: List<ValidationError>): ValidationResult<Nothing> =
			ValidationResult(null, errors)
	}
}
