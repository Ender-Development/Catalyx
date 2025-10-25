package org.ender_development.catalyx.utils.validation

class FieldValidationBuilder<V>(private val value: V?, private val fieldName: String, private val parentBuilder: ValidationBuilder<*>) {
	private var currentValue: V? = value

	fun validate(validator: IValidator<V?>): FieldValidationBuilder<V> {
		if (currentValue != null && !validator.validate(currentValue)) {
			parentBuilder.addError(fieldName, "Validation failed for field '$fieldName'")
			currentValue = null
		} else if (currentValue == null && !validator.validate(null))
			parentBuilder.addError(fieldName, "Field '$fieldName' is required")
		return this
	}

	fun withMessage(message: String): FieldValidationBuilder<V> {
        // Remove the last error and replace with custom message
        val errors = parentBuilder.getErrors().toMutableList()
        if (errors.isNotEmpty() && errors.last().field == fieldName) {
            errors.removeLast()
            parentBuilder.addError(fieldName, message)
        }
        return this
    }

	fun orElse(defaultValue: V): V =
		currentValue ?: defaultValue

	fun get(): V? =
		currentValue
}
