package org.ender_development.catalyx.api.v1.common.extensions

import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationResult
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidator
import org.ender_development.catalyx.core.validation.ValidationBuilder
import org.ender_development.catalyx.core.validation.ValidationResult

fun <T> T?.validateWith(vararg validators: IValidator<T?>): IValidationResult<T> {
	val builder = ValidationBuilder<T>()

	val error = validators.any {
		!it.validate(this)
	}
	if(!error)
		return builder.build(this)

	builder.addError(null, "Validation failed")
	return builder.build(null)
}

inline fun <T> validate(data: T?, block: ValidationBuilder<T>.() -> Unit) =
	ValidationBuilder<T>().let {
		it.block()
		it.build(data)
	}

inline fun <T> validate(block: ValidationBuilder<T>.() -> T?): ValidationResult<T> {
    val builder = ValidationBuilder<T>()
    val data = builder.block()
    return builder.build(data)
}
