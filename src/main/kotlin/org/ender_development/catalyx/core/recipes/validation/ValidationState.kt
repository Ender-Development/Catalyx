package org.ender_development.catalyx.core.recipes.validation

enum class ValidationState {
	VALID, INVALID, SKIP
}

data class Result<T>(val state: ValidationState, val recipe: T)
