package org.ender_development.catalyx.api.v1.validation

import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.extensions.toBlock
import org.ender_development.catalyx.api.v1.common.extensions.toBlockState
import org.ender_development.catalyx.api.v1.common.extensions.toItem
import org.ender_development.catalyx.api.v1.common.extensions.toStack
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidator
import org.ender_development.catalyx.core.config.ConfigParser

@Suppress("UNUSED")
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

	fun atLeast(value: Number): IValidator<Number?> =
		IValidator { it != null && it.toDouble() >= value.toDouble() }

	fun atMost(value: Number): IValidator<Number?> =
		IValidator { it != null && it.toDouble() <= value.toDouble() }

	fun <T> oneOf(vararg allowed: T): IValidator<T?> =
		IValidator { it != null && it in allowed }

	fun <T> listAll(elementValidator: IValidator<T?>): IValidator<List<T>?> =
		IValidator { list ->
			list != null && list.all(elementValidator::validate)
		}

	fun <K, V> mapAll(keyValidator: IValidator<K?>, valueValidator: IValidator<V?>): IValidator<Map<K, V>?> =
		IValidator { map ->
			map != null && map.all { (key, value) ->
				keyValidator.validate(key) && valueValidator.validate(value)
			}
		}

	fun isItemStack(): IValidator<String?> =
		IValidator { it != null && it.toStack() != ItemStack.EMPTY }

	fun isBlockState(): IValidator<String?> =
		IValidator { it != null && it.toBlockState() != null }

	fun isBlock(): IValidator<String?> =
		IValidator { it != null && it.toBlock() != null }

	fun isItem(): IValidator<String?> =
		IValidator { it != null && it.toItem() != null }
}
