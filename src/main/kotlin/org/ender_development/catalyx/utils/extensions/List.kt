package org.ender_development.catalyx.utils.extensions

import com.google.common.collect.ImmutableList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.utils.validation.ValidationError
import org.ender_development.catalyx.utils.validation.ValidationResult

fun List<ItemStack>.containsItem(stack: ItemStack, strict: Boolean = false) =
	any { OreDictionary.itemMatches(it, stack, strict) }

fun <T> List<T>.toImmutableList(): ImmutableList<T> =
	ImmutableList.copyOf(this)

fun <T> List<T>.toSingletonList(): List<T> =
	ObjectLists.singleton(this[0])

fun <T> List<T>.validateEach(validator: (idx: Int, T) -> ValidationResult<T>) =
	mapIndexed(validator)

@JvmName("copyOfIS")
fun List<ItemStack>.copyOf() =
	map {
		if(it.isEmpty)
			ItemStack.EMPTY
		else
			it.copy()
	}

@JvmName("copyOfFS")
fun List<FluidStack>.copyOf() =
	map { it.copy() }

inline fun <T, R> List<T>.mapUnique(transform: (T) -> R) =
	mapTo(hashSetOf(), transform)

fun List<ValidationError>.getBySeverity(severity: ValidationError.Severity) =
	filter { it.severity == severity }

fun List<ValidationError>.getByMinSeverity(severity: ValidationError.Severity) =
	filter { it.severity >= severity }
