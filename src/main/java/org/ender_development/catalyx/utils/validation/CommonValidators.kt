package org.ender_development.catalyx.utils.validation

import org.ender_development.catalyx.config.ConfigParser

object CommonValidators {
	fun <T> notNull(): IValidator<T?> =
		IValidator { it != null }

	fun notBlank(): IValidator<String?> =
		IValidator { !it.isNullOrBlank() }

	fun notEmpty(): IValidator<String?> =
		IValidator { !it.isNullOrEmpty() }

	fun minLength(length: Int): IValidator<String?> =
		IValidator { (it?.length ?: 0) >= length }

	fun maxLength(length: Int): IValidator<String?> =
		IValidator { (it?.length ?: 0) <= length }

	fun range(min: Int, max: Int): IValidator<Int?> =
		IValidator { it != null && it in min..max }

	fun positive(): IValidator<Number?> =
		IValidator { it != null && it.toDouble() > 0 }

	fun negative(): IValidator<Number?> =
		IValidator { it != null && it.toDouble() < 0 }

	fun atLeast(value: Comparable<Number>): IValidator<Comparable<Number>?> =
		IValidator { it != null && it >= value as Number }

	fun atMost(value: Comparable<Number>): IValidator<Comparable<Number>?> =
		IValidator { it != null && it <= value as Number }

	fun <T> oneOf(vararg values: T): IValidator<T?> =
		IValidator { it != null && values.contains(it) }

	fun isItemStack(): IValidator<String?> = IValidator {
		if(it == null)
			return@IValidator false

		return@IValidator try {
			ConfigParser.ConfigItemStack(it).toItemStack()
			true
		} catch(_: Exception) {
			false
		}
	}

	fun isBlockState(): IValidator<String?> = IValidator {
		if(it == null)
			return@IValidator false

		return@IValidator try {
			ConfigParser.ConfigBlockState(it).state != null
		} catch(_: Exception) {
			false
		}
	}

	fun isBlock(): IValidator<String?> = IValidator {
		if(it == null)
			return@IValidator false

		return@IValidator try {
			ConfigParser.ConfigBlockState(it).block != null
		} catch(_: Exception) {
			false
		}
	}
}
