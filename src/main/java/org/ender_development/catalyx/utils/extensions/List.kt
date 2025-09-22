package org.ender_development.catalyx.utils.extensions

import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary

fun List<ItemStack>.containsItem(stack: ItemStack, strict: Boolean = false): Boolean =
	any { OreDictionary.itemMatches(it, stack, strict) }

fun <T> List<T>.toImmutable(): ImmutableList<T> = ImmutableList.copyOf(this)

@JvmName("copyOfIS")
fun List<ItemStack>.copyOf(): List<ItemStack> =
	map { if(it.isEmpty) ItemStack.EMPTY else it.copy() }

@JvmName("copyOfFS")
fun List<FluidStack>.copyOf(): List<FluidStack> =
	map { it.copy() }
