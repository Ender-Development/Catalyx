package org.ender_development.catalyx.api.v1.validation.interfaces

import org.ender_development.catalyx.core.validation.FieldValidationBuilder

interface IFieldValidationBuilder<V> {
	// TODO ask Ender for whether these KDocs are accurate/correct, this is his system after all
	// this is kinda confusing to document

	/**
	 * Validate the current value of this builder with the [validator].
	 */
	fun validate(validator: IValidator<V?>): IFieldValidationBuilder<V>

	/**
	 * If the last message in the parent validator is related to this field, replace it with the passed in [message].
	 */
	fun withMessage(message: String): FieldValidationBuilder<V>

	/**
	 * Get the final validated field, or, if null, the specified [defaultValue].
	 */
	fun orElse(defaultValue: V): V

	/**
	 * Get the final validated field.
	 */
	fun get(): V?
}
