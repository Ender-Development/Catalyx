package org.ender_development.catalyx.api.v1.validation

import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.validation.interfaces.IFieldValidationBuilder
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationBuilder
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationResult
import org.ender_development.catalyx.core.validation.ValidationError
import org.ender_development.catalyx.core.validation.ValidationResult

object Validation {
	fun newValidationError(
		field: String? = null,
		message: String,
		code: String? = null,
		severity: Severity = Severity.ERROR,
	): IValidationError = ValidationError(field, message, code, severity)

	fun <T> newValidationBuilder(): IValidationBuilder<T> = TODO()
	fun <V> newFieldValidationBuilder(): IFieldValidationBuilder<V> = TODO()

	fun <T> IValidationResult<T>.success(data: T): IValidationResult<T>
		= ValidationResult.success(data)

	fun IValidationResult<*>.failure(errors: List<IValidationError>): IValidationResult<*>
		= ValidationResult.failure(errors)
}
