package org.ender_development.catalyx.core.validation

import org.ender_development.catalyx.api.v1.validation.interfaces.IFieldValidationBuilder
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidator

class FieldValidationBuilder<V>(value: V?, private val fieldName: String, private val parentBuilder: ValidationBuilder<*>) : IFieldValidationBuilder<V> {
	private var currentValue: V? = value

	override fun validate(validator: IValidator<V?>): FieldValidationBuilder<V> {
		if(currentValue != null && !validator.validate(currentValue)) {
			parentBuilder.addError(fieldName, "Validation failed for field '$fieldName'")
			currentValue = null
		} else if(currentValue == null && !validator.validate(null))
			parentBuilder.addError(fieldName, "Field '$fieldName' is required")
		return this
	}

	override fun withMessage(message: String): FieldValidationBuilder<V> {
        // Remove the last error and replace with custom message
		parentBuilder.errors.lastOrNull()?.let {
			if(it.field == fieldName) {
				parentBuilder.mutableErrors.removeLast()
				parentBuilder.addError(fieldName, message)
			}
		}
        return this
    }

	override fun orElse(defaultValue: V): V =
		currentValue ?: defaultValue

	override fun get(): V? =
		currentValue
}
