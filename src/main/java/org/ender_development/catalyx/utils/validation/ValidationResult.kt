package org.ender_development.catalyx.utils.validation

import kotlin.collections.map

sealed class ValidationResult<out T> {
	data class Success<T>(val data: T) : ValidationResult<T>()
	data class Failure(val errors: List<ValidationError>) : ValidationResult<Nothing>()

	fun isSuccess(): Boolean =
		this is Success<T>

	fun isFailure(): Boolean =
		this is Failure

	fun getDataOrNull(): T? =
		when(this) {
			is Success -> this.data
			is Failure -> null
		}

	fun getErrors(): List<ValidationError> =
		when(this) {
			is Success -> emptyList<ValidationError>()
			is Failure -> this.errors
		}

	fun getErrorMessages() =
		getErrors().map { it.message }
}
