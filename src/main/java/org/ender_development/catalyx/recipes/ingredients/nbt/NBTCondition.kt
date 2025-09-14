package org.ender_development.catalyx.recipes.ingredients.nbt

import org.ender_development.catalyx.Catalyx
import java.util.Objects

open class NBTCondition {
	companion object {
		val ANY = NBTCondition()

		fun create(tagType: TagType, nbtKey: String, value: Any?) =
			if(tagType == TagType.LIST)
				throw IllegalArgumentException("Use NBTListCondition::create instead of NBTCondition::create")
			else
				NBTCondition(tagType, nbtKey, value)
	}

	internal val tagType: TagType?
	internal val nbtKey: String?
	internal val value: Any?

	constructor() : this(null, null, null)
	constructor(tagType: TagType?, nbtKey: String?, value: Any?) {
		this.tagType = tagType
		this.nbtKey = nbtKey
		this.value = value
		if(tagType == null || nbtKey == null || value == null)
			Catalyx.logger.error("Creating NBTCondition with null values: tagType=$tagType, nbtKey=$nbtKey, value=$value", Throwable())
	}

	override fun toString() =
		"$nbtKey: $value"

	override fun hashCode() =
		Objects.hash(tagType, nbtKey, value)

	override fun equals(other: Any?) =
		this === other || (other is NBTCondition && tagType == other.tagType && nbtKey.equals(other.nbtKey) && value == other.value)
}
