package org.ender_development.catalyx.utils.validation

import kotlin.collections.map

// roz: this might as well be a normal class with an enum
sealed class ValidationResult<out T> {
	data class Success<T>(val underlyingData: T) : ValidationResult<T>()
	// roz: if [errors] in [Failure] is supposed to be a [MutableList], switch [errors] & [errorMessages] to getters
	data class Failure(val underlyingErrors: List<ValidationError>) : ValidationResult<Nothing>()

	val success = this is Success<T>
	val failure = this is Failure

	val data =
		when(this) {
			is Success -> underlyingData
			is Failure -> null
		}

	val errors =
		when(this) {
			is Success -> emptyList()
			is Failure -> underlyingErrors
		}

	val errorMessages = errors.map(ValidationError::message)
}
