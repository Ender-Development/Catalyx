package org.ender_development.catalyx.core.validation

import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationBuilder
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidator

@Suppress("UNUSED")
class ValidationBuilder<T> : IValidationBuilder<T> {
	internal val errors = mutableListOf<ValidationError>()
	private var target: T? = null

	fun <V> field(value: V?, fieldName: String, vararg validators: IValidator<V?>): FieldValidationBuilder<V> =
		FieldValidationBuilder(value, fieldName, this).apply {
			validators.forEach(::validate)
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

    fun rule(condition: Boolean, message: String, severity: Severity = Severity.ERROR) {
        if(!condition)
            errors.add(ValidationError(null, message, null, severity))
    }

    fun addError(field: String? = null, message: String, code: String? = null, severity: Severity = Severity.ERROR) =
        errors.add(ValidationError(field, message, code, severity))

	fun addWarning(field: String? = null, message: String, code: String? = null) =
        addError(field, message, code, Severity.WARNING)

	fun build(data: T?): ValidationResult<T> {
        val onlyWarnings = errors.none { it.severity != Severity.WARNING }

        return if(onlyWarnings && data != null)
            ValidationResult.success(data)
        else {
            if(data == null && errors.isEmpty())
                errors.add(ValidationError(message = "Data construction failed"))
            ValidationResult.failure(errors)
        }
    }

	fun hasErrors(): Boolean =
		errors.any { it.severity != Severity.WARNING }

    fun hasWarnings(): Boolean =
		errors.any { it.severity == Severity.WARNING }

    fun getErrors(): List<ValidationError> = // TODO replace with new kt 2.3.0 feature
		errors
}
