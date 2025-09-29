package org.ender_development.catalyx.recipes.validation

import org.ender_development.catalyx.recipes.Validation.ValidationStatus

class ValidationResult<T>(val type: ValidationStatus, val result: T) {
	companion object {
		fun <T> newResult(type: ValidationStatus, result: T) = ValidationResult(type, result)
	}
}
