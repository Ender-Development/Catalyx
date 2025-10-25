package org.ender_development.catalyx.recipes.validation

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx

@Deprecated("Needs to be refactored to use the new validation system")
class Validator {
	private val errorMessages = mutableListOf<String>()

	val status: ValidationState
		get() = when {
			isValid -> ValidationState.VALID
			else -> ValidationState.INVALID
		}

	val isValid: Boolean
		get() = errorMessages.isEmpty()

	/**
	 * All error messages concatenated into a single string, separated by new lines
	 */
	val message: String
		get() = errorMessages.joinToString("\n")

	/**
	 * All error messages as a list of strings
	 */
	val messages: List<String> = errorMessages // typecast down to prevent modification

	/** Add an error message to the validator
	 * @param message The error message to add
	 */
	fun error(message: String) =
		errorMessages.add(message)

	/**
	 * Add an error message to the validator if the given expression is **true**
	 * @param error The result of an expression
	 * @param message The error message to add if the expression is true
	 */
	@Suppress("NOTHING_TO_INLINE")
	inline fun error(error: Boolean, message: String) {
		if(error)
			error(message)
	}

	/**
	 * Add an error message to the validator if the given expression is **false**
	 * @param assertion The result of an expression
	 * @param message The error message to add if the expression is false
	 */
	@Suppress("NOTHING_TO_INLINE")
	inline fun assert(assertion: Boolean, message: String) =
		error(!assertion, message)

	/**
	 * Log all error messages using the specified logger
	 * @param logger The logger to use, defaults to [Catalyx.LOGGER]
	 * @param initialMsg An optional initial message to log before the error messages
	 */
	fun logMessages(logger: Logger = Catalyx.LOGGER, initialMsg: String = "") =
		errorMessages.forEachIndexed { index, string ->
			if(index == 0 && initialMsg.isNotEmpty())
				logger.error(initialMsg)
			logger.error((if(initialMsg.isNotEmpty()) "    " else "") + string)
		}
}
