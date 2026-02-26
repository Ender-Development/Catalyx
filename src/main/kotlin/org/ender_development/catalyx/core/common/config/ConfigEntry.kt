package org.ender_development.catalyx.core.common.config

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.extensions.toBlock
import org.ender_development.catalyx.api.v1.common.extensions.toBlockState
import org.ender_development.catalyx.api.v1.common.extensions.toItem
import org.ender_development.catalyx.api.v1.common.extensions.toStack
import java.util.Objects

open class GenericConfigEntry<T> {
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

	constructor(input: String, parser: (String) -> T) {
		var split = input.split(";")
		if(split.size > 2)
			error("Invalid Config Entry: $input")
		value = if(split.size == 2)
			parser.invoke(split[1])
		else
			null
		split = split[0].split(":")
		if(split.size !in 2..3)
			error("Invalid Config Entry: $input")
		mod = split[0]
		id = split[1]
		meta = if(split.size != 3 || split[2] == "-1")
			IGNORE_META
		else
			split[2].toInt()
	}

	override fun equals(other: Any?): Boolean {
		return when (other) {
			is Item -> other == item
			is ItemStack -> if(meta == IGNORE_META)
					other.isItemEqualIgnoreDurability(itemStack)
				else
					other.isItemEqual(itemStack)
			is Block -> other == block
			is IBlockState -> other == blockState
			else -> this === other
		}
	}

	override fun hashCode(): Int =
		Objects.hash(mod, id, meta, value)
}

class ConfigEntry(input: String) : GenericConfigEntry<String>(input, String::toString)
class ConfigEntryWithInt(input: String) : GenericConfigEntry<Int>(input, String::toInt)
class ConfigEntryWithFloat(input: String) : GenericConfigEntry<Float>(input, String::toFloat)
class ConfigEntryWithBoolean(input: String) : GenericConfigEntry<Boolean>(input, String::toBoolean)
