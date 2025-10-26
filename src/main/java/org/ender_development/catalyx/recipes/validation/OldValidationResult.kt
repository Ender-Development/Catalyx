package org.ender_development.catalyx.recipes.validation

@Deprecated("Use ValidationResult in utils.validation package instead", ReplaceWith("org.ender_development.catalyx.utils.validation.ValidationResult"))
class OldValidationResult<T>(val type: ValidationState, val result: T)
