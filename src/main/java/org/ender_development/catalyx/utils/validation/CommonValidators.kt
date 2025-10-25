package org.ender_development.catalyx.utils.validation

import org.ender_development.catalyx.config.ConfigParser

object CommonValidators {
	fun <T> notNull(): IValidator<T?> =
		IValidator<T?> { it != null }

	fun notBlank(): IValidator<String?> =
		IValidator<String?> { !it.isNullOrBlank() }

	fun notEmpty(): IValidator<String?> =
		IValidator<String?> { !it.isNullOrEmpty() }

	fun minLength(length: Int): IValidator<String?> =
		IValidator<String?> { (it?.length ?: 0) >= length }

	fun maxLength(length: Int): IValidator<String?> =
		IValidator<String?> { (it?.length ?: 0) <= length }

	fun range(min: Int, max: Int): IValidator<Int?> =
		IValidator<Int?> { it != null && it in min..max }

	fun positive(): IValidator<Number?> =
		IValidator<Number?> { it != null && it.toDouble() > 0 }

	fun negative(): IValidator<Number?> =
		IValidator<Number?> { it != null && it.toDouble() < 0 }

	fun <T> oneOf(vararg values: T): IValidator<T?> =
		IValidator<T?> { it != null && values.contains(it) }

	fun <T> custom(validationLogic: (T?) -> Boolean): IValidator<T?> =
		IValidator<T?> { validationLogic(it) }

	fun isItemStack(): IValidator<String?> = IValidator<String?> {
		it?.let {
			try {
				ConfigParser.ConfigItemStack(it).toItemStack()
			} catch(_: Exception) {
				false
			}
			true
		}
		false
	}

	fun isBlockState(): IValidator<String?> = IValidator<String?> {
		it?.let {
			try {
				ConfigParser.ConfigBlockState(it).state.let { true }
			} catch(_: Exception) {
			}
		}
		false
	}
}
