package org.ender_development.catalyx.recipes.validation

class ValidationResult<T>(val type: ValidationState, val result: T) {
	companion object {
		fun <T> newResult(type: ValidationState, result: T) = ValidationResult(type, result)
	}
}
