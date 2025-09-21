package org.ender_development.catalyx.recipes

import org.ender_development.catalyx.Catalyx

class Validator {
	private val errorMessages = mutableListOf<String>()

	val isValid: Boolean
		get() = errorMessages.isEmpty()

	val message: String
		get() = errorMessages.joinToString("\n")

	fun error(message: String) =
		errorMessages.add(message)

	fun listMessages() =
		errorMessages.toList()

	fun logMessages() =
		errorMessages.forEach(Catalyx.logger::error)
}
