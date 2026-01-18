package org.ender_development.catalyx.core.recipes.validation

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.validateWith
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError

class Validator {
	private val errors = mutableListOf<IValidationError>()

	fun error(condition: Boolean, message: String) =
		errors.addAll(message.validateWith({ !condition }).errors)

	fun assert(condition: Boolean, message: String) =
		error(!condition, message)

	fun logErrors(logger: Logger = Catalyx.LOGGER, initialMsg: String = "") =
		errors.forEachIndexed { index, error ->
			if(index == 0 && initialMsg.isNotEmpty())
				logger.error(initialMsg)
			logger.error((if(initialMsg.isNotEmpty()) "    " else "") + error.message)
		}

	val status: ValidationState
		get() = when {
			errors.isEmpty() -> ValidationState.VALID
			else -> ValidationState.INVALID
		}
}
