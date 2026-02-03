@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import com.google.common.collect.ImmutableList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.api.v1.common.Severity
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationError
import org.ender_development.catalyx.api.v1.validation.interfaces.IValidationResult

fun List<ItemStack>.containsItem(stack: ItemStack, strict: Boolean = false) =
	any { OreDictionary.itemMatches(it, stack, strict) }

fun <T> List<T>.toImmutableList(): ImmutableList<T> =
	ImmutableList.copyOf(this)

fun <T> List<T>.toSingletonList(): List<T> =
	ObjectLists.singleton(this[0])

fun <T> List<T>.validateEach(validator: (idx: Int, T) -> IValidationResult<T>) =
	mapIndexed(validator)

@JvmName("copyOfIS")
inline fun List<ItemStack>.copyOf() =
	map {
		it.orIfNotEmpty(ItemStack::copy)
	}

@JvmName("copyOfFS")
inline fun List<FluidStack>.copyOf() =
	map(FluidStack::copy)

inline fun <T, R> List<T>.mapUnique(transform: (T) -> R) =
	mapTo(hashSetOf(), transform)

fun List<IValidationError>.getBySeverity(severity: Severity) =
	filter { it.severity == severity }

fun List<IValidationError>.getByMinSeverity(severity: Severity) =
	filter { it.severity >= severity }

/**
 * Get the specified [index][idx] in the list and apply the [mapper] function, or return the [default]
 */
inline fun <T, R> List<T>.getApplyOrDefault(idx: Int, crossinline mapper: (T) -> R, crossinline default: () -> R) =
	if(idx in indices)
		mapper(this[idx])
	else
		default()

inline fun <T, reified R> List<T>.mapToArray(crossinline mapper: (T) -> R) =
	Array(size) { mapper(this[it]) }
