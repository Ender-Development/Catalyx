package org.ender_development.catalyx.utils.extensions

import org.ender_development.catalyx.utils.validation.IValidator
import org.ender_development.catalyx.utils.validation.ValidationBuilder
import org.ender_development.catalyx.utils.validation.ValidationResult

fun <T> T?.validateWith(vararg validators: IValidator<T?>): ValidationResult<T> {
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
