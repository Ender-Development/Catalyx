package org.ender_development.catalyx.utils.validation

fun <T> T?.validateWith(vararg validators: IValidator<T?>): ValidationResult<T> {
	val builder = ValidationBuilder<T>()
	var result = this

	validators.forEach {
		if (!it.validate(result)) {
			builder.addError(null, "Validation failed")
			result = null
		}
	}
	return builder.build(result)
}

inline fun <T> validate(data: T?, block: ValidationBuilder<T>.() -> Unit): ValidationResult<T> {
	val builder = ValidationBuilder<T>()
	builder.block()
	return builder.build(data)
}

fun <T> List<T>.validateEach(validator: (T, Int) -> ValidationResult<T>): List<ValidationResult<T>> =
	this.mapIndexed { index, item -> validator(item, index) }

fun List<ValidationError>.getBySeverity(severity: ValidationError.Severity): List<ValidationError> =
	this.filter { it.severity == severity }

fun List<ValidationError>.getByMinSeverity(severity: ValidationError.Severity): List<ValidationError> =
	this.filter { it.severity.ordinal >= severity.ordinal }
