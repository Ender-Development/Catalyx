package org.ender_development.catalyx.recipes

import org.ender_development.catalyx.Catalyx

class Validator {
	private val errorMessages = mutableListOf<String>()

	val status: ValidationStatus
		get() = when {
			errorMessages.isEmpty() -> ValidationStatus.VALID
			else -> ValidationStatus.INVALID
		}

	val isValid: Boolean
		get() = errorMessages.isEmpty()

	val message: String
		get() = errorMessages.joinToString("\n")

	val messages: List<String> = errorMessages // typecast down to prevent modification

	fun error(message: String) =
		errorMessages.add(message)

	fun logMessages() =
		errorMessages.forEach(Catalyx.logger::error)

	enum class ValidationStatus {
		VALID, INVALID, WARNING
	}
}
