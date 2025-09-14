package org.ender_development.catalyx.recipes

import org.ender_development.catalyx.Catalyx

class Validator {
	private val errorMessages = mutableListOf<String>()

	val isValid: Boolean
		get() = errorMessages.isEmpty()

	fun error(message: String) = errorMessages.add(message)

	fun getMessage(): String = errorMessages.joinToString("\n")

	fun listMessages(): List<String> = errorMessages.toList()

	fun logMessages() = errorMessages.forEach(Catalyx.logger::error)
}
