package org.ender_development.catalyx.utils.validation

class ValidationBuilder<T> {
	private val errors = mutableListOf<ValidationError>()
	private var target: T? = null

	fun <V> field(value: V?, fieldName: String, vararg validators: IValidator<V?>): FieldValidationBuilder<V> =
		FieldValidationBuilder(value, fieldName, this).apply {
			validators.forEach { validate(it) }
		}

	fun <V> validate(value: V?, fieldName: String, condition: (V) -> Boolean, errorMessage: String? = null): V? =
		when {
            value == null -> {
                addError(fieldName, errorMessage ?: "Field '$fieldName' is null or missing")
                null
            }
            !condition(value) -> {
                addError(fieldName, errorMessage ?: "Field '$fieldName' failed validation: $value")
                null
            }
            else -> value
        }

    fun rule(condition: Boolean, message: String, severity: ValidationError.Severity = ValidationError.Severity.ERROR) {
        if (!condition)
            errors.add(ValidationError(null, message, null, severity))
    }

    fun addError(field: String? = null, message: String, code: String? = null, severity: ValidationError.Severity = ValidationError.Severity.ERROR) =
        errors.add(ValidationError(field, message, code, severity))

	fun addWarning(field: String? = null, message: String, code: String? = null) =
        addError(field, message, code, ValidationError.Severity.WARNING)

	fun build(data: T?): ValidationResult<T> {
        val criticalErrors = errors.filter { it.severity == ValidationError.Severity.CRITICAL }
        val regularErrors = errors.filter { it.severity == ValidationError.Severity.ERROR }

        return if ((criticalErrors.isEmpty() && regularErrors.isEmpty()) && data != null) {
            ValidationResult.Success(data)
        } else {
            if (data == null && errors.isEmpty()) {
                errors.add(ValidationError(message = "Data construction failed"))
            }
            ValidationResult.Failure(errors.toList())
        }
    }

	fun hasErrors(): Boolean =
		errors.any { it.severity != ValidationError.Severity.WARNING }

    fun hasWarnings(): Boolean =
		errors.any { it.severity == ValidationError.Severity.WARNING }

    fun getErrors(): List<ValidationError> =
		errors.toList()
}
