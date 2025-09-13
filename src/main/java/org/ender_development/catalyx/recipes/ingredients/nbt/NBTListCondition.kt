package org.ender_development.catalyx.recipes.ingredients.nbt

import net.minecraft.nbt.NBTBase
import org.ender_development.catalyx.Catalyx
import java.util.Objects

class NBTListCondition: NBTCondition {
	companion object {
		fun create(listTagType: TagType, nbtKey: String, value: List<NBTBase>) = NBTListCondition(listTagType, nbtKey, value)
	}

	internal val listTagType: TagType?

	constructor(listTagType: TagType?, nbtKey: String?, value: List<NBTBase>?): super(TagType.LIST, nbtKey, value) {
		this.listTagType = listTagType
		if (listTagType == null) Catalyx.logger.error("NBTListCondition must not have null parameters", Throwable())
	}

	override fun toString(): String = "$nbtKey (type $listTagType): $value"

	override fun hashCode(): Int = Objects.hash(tagType, nbtKey, value, listTagType)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other is NBTListCondition) {
			val o = other as NBTListCondition?
			return o != null && tagType == o.tagType && nbtKey.equals(o.nbtKey) && value == o.value && listTagType == o.listTagType
		}
		return false
	}
}
