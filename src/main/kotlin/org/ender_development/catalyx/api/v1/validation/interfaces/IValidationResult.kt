package org.ender_development.catalyx.api.v1.validation.interfaces

interface IValidationResult<T> {
	val data: T?
	val errors: List<IValidationError>
	val success: Boolean
		get() = errors.isEmpty()
	val failure: Boolean
		get() = !success
	val errorMessages: List<String>
		get() = errors.map(IValidationError::message)
}
