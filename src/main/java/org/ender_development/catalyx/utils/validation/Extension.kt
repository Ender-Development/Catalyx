package org.ender_development.catalyx.utils.validation

// roz: move this to catalyx/utils/extensions please

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

fun <T> List<T>.validateEach(validator: (idx: Int, T) -> ValidationResult<T>) =
	mapIndexed(validator)

fun List<ValidationError>.getBySeverity(severity: ValidationError.Severity) =
	filter { it.severity == severity }

fun List<ValidationError>.getByMinSeverity(severity: ValidationError.Severity) =
	filter { it.severity >= severity }
