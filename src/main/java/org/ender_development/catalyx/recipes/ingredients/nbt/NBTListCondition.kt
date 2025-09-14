package org.ender_development.catalyx.recipes.ingredients.nbt

import net.minecraft.nbt.NBTBase
import java.util.*

class NBTListCondition : NBTCondition {
	companion object {
		fun create(listTagType: TagType, nbtKey: String, value: List<NBTBase>) =
			NBTListCondition(listTagType, nbtKey, value)
	}

	internal val listTagType: TagType

	constructor(listTagType: TagType, nbtKey: String?, value: List<NBTBase>?) : super(TagType.LIST, nbtKey, value) {
		this.listTagType = listTagType
	}

	override fun toString() =
		"$nbtKey (type $listTagType): $value"

	override fun hashCode() =
		Objects.hash(tagType, nbtKey, value, listTagType)

	override fun equals(other: Any?) =
		this === other || (other is NBTListCondition && tagType == other.tagType && nbtKey.equals(other.nbtKey) && value == other.value && listTagType == other.listTagType)
}
