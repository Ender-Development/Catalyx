package org.ender_development.catalyx.core.common.config

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.extensions.*
import java.util.*

/**
 * A generic class that takes in an input with the form "mod:id:meta;value", and parses it out into [mod], [id], [meta] and [value] (both value and meta are considered optional)
 */
open class GenericConfigEntry<T>(input: String, parser: (String) -> T) {
	companion object {
		const val IGNORE_META = -1
	}

	val mod: String
	val id: String
	val meta: Int
	val value: T?

	val item: Item?
		get() = "$mod:$id".toItem()

	val itemStack: ItemStack
		get() = "$mod:$id".toStack(meta = meta)

	val block: Block?
		get() = "$mod:$id".toBlock()

	val blockState: IBlockState?
		get() = "$mod:$id".toBlockState(if(meta == IGNORE_META) 0 else meta)

	init {
		// input is "mod:id:meta;value", both value and meta are optional
		val split = input.split(';')
		value = when(split.size) {
			1 -> null
			2 -> parser(split[1])
			else -> error("Invalid config entry - too many or too few semicolon-separated fields: '$input'")
		}

		val itemSplit = split[0].split(':')
		if(itemSplit.size != 2 && itemSplit.size != 3)
			error("Invalid config entry - too many or too few colon-separated fields: '$input'")

		mod = itemSplit[0]
		id = itemSplit[1]
		meta = itemSplit.getApplyOrDefault(2, String::toInt, ::IGNORE_META)
	}

	override fun equals(other: Any?) =
		this === other || when(other) {
			is Item -> other == item
			is ItemStack -> if(meta == IGNORE_META)
				other.isItemEqualIgnoreDurability(itemStack)
			else
				other.isItemEqual(itemStack)
			is Block -> other == block
			is IBlockState -> other == blockState
			else -> false
		}

	override fun hashCode() =
		Objects.hash(mod, id, meta, value)
}

class ConfigEntry(input: String) : GenericConfigEntry<String>(input, String::toString)
class ConfigEntryWithInt(input: String) : GenericConfigEntry<Int>(input, String::toInt)
class ConfigEntryWithFloat(input: String) : GenericConfigEntry<Float>(input, String::toFloat)
class ConfigEntryWithBoolean(input: String) : GenericConfigEntry<Boolean>(input, String::toBoolean)
