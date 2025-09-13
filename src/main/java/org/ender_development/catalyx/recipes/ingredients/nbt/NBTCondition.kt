package org.ender_development.catalyx.recipes.ingredients.nbt

import org.ender_development.catalyx.Catalyx
import java.util.Objects

open class NBTCondition {
	companion object {
		val ANY = NBTCondition()

		fun create(tagType: TagType, nbtKey: String, value: Any?): NBTCondition {
			if (tagType == TagType.LIST) throw IllegalArgumentException("Use ListNBTCondition::create instead of NBTCondition::create")
			return NBTCondition(tagType, nbtKey, value)
		}
	}

	internal val tagType: TagType?
	internal val nbtKey: String?
	internal val value: Any?

	constructor() : this(null, null, null)
	constructor(tagType: TagType?, nbtKey: String?, value: Any?) {
		this.tagType = tagType
		this.nbtKey = nbtKey
		this.value = value
		if (tagType == null || nbtKey == null || value == null) Catalyx.logger.error("Creating NBTCondition with null values: tagType=$tagType, nbtKey=$nbtKey, value=$value", Throwable())
	}

	override fun toString(): String = "$nbtKey: $value"

	override fun hashCode(): Int = Objects.hash(tagType, nbtKey, value)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other is NBTCondition) {
			val o = other as NBTCondition?
			return o != null && tagType == o.tagType && nbtKey.equals(o.nbtKey) && value == o.value
		}
		return false
	}
}
