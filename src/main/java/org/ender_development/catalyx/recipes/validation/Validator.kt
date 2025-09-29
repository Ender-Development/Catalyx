package org.ender_development.catalyx.recipes.validation

import org.ender_development.catalyx.Catalyx

class Validator {
	private val errorMessages = mutableListOf<String>()

	val status: ValidationState
		get() = when {
			isValid -> ValidationState.VALID
			else -> ValidationState.INVALID
		}

	val isValid: Boolean
		get() = errorMessages.isEmpty()

	val message: String
		get() = errorMessages.joinToString("\n")

	val messages: List<String> = errorMessages // typecast down to prevent modification

	fun error(message: String) =
		errorMessages.add(message)

	fun logMessages() =
		errorMessages.forEach(Catalyx.LOGGER::error)
}
