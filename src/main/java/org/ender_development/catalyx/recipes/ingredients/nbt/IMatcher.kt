package org.ender_development.catalyx.recipes.ingredients.nbt

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.utils.extensions.getLongArray

interface IMatcher {
	companion object {
		fun hasKey(tag: NBTTagCompound?, key: String, tagType: Int) =
			tag?.hasKey(key, tagType) == true
	}

	fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean

	fun evaluate(stack: ItemStack, condition: NBTCondition?) =
		evaluate(stack.tagCompound, condition)

	fun evaluate(stack: FluidStack, condition: NBTCondition?) =
		evaluate(stack.tag, condition)

	/**
	 * Return true without checking if the NBT tags actually match or even exist.
	 */
	object ANY : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?) =
			true
	}

	private interface InequalityBase : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null)
				return false

			if(!TagType.isNumeric(condition.tagType) || !hasKey(tag, condition.nbtKey!!, condition.tagType.typeId))
				return false

			return inequality(tag.getLong(condition.nbtKey), (condition.value as Number).toLong())
		}

		fun inequality(tagValue: Long, conditionValue: Long): Boolean
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is less than the value specified in the condition.
	 */
	object LESS_THAN : InequalityBase {
		override fun inequality(tagValue: Long, conditionValue: Long) =
			tagValue < conditionValue
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is less than or equal to the value specified in the condition.
	 */
	object LESS_THAN_OR_EQUAL : InequalityBase {
		override fun inequality(tagValue: Long, conditionValue: Long) =
			tagValue <= conditionValue
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is greater than the value specified in the condition.
	 */
	object GREATER_THAN : InequalityBase {
		override fun inequality(tagValue: Long, conditionValue: Long) =
			tagValue > conditionValue
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is greater than or equal to the value specified in the condition.
	 */
	object GREATER_THAN_OR_EQUAL : InequalityBase {
		override fun inequality(tagValue: Long, conditionValue: Long) =
			tagValue >= conditionValue
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is equal to the value specified in the condition.
	 */
	object EQUAL_TO : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null)
				return false

			if(!hasKey(tag, condition.nbtKey!!, condition.tagType.typeId))
				return false

			return if(TagType.isNumeric(condition.tagType))
				tag.getLong(condition.nbtKey) == (condition.value as Number).toLong()
			else
				when(condition.tagType) {
					TagType.BYTE_ARRAY -> tag.getByteArray(condition.nbtKey) contentEquals condition.value as ByteArray
					TagType.INT_ARRAY -> tag.getIntArray(condition.nbtKey) contentEquals condition.value as IntArray
					TagType.LONG_ARRAY -> tag.getLongArray(condition.nbtKey) contentEquals condition.value as LongArray
					TagType.LIST -> condition is NBTListCondition && tag.getTagList(condition.nbtKey, condition.listTagType.typeId).tagList == condition.value
					TagType.STRING -> tag.getString(condition.nbtKey) == condition.value
					TagType.COMPOUND -> tag.getCompoundTag(condition.nbtKey) == condition.value
					else -> throw IllegalStateException("TagType#isNumeric returned false on a numeric TagType")
				}
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is equal to the value specified in the condition.
	 * If the value is a compound tag, it will recursively check if all keys and values match.
	 */
	object RECURSIVE_EQUAL_TO : IMatcher { // TODO: this and EQUAL_TO are basically the same matcher except for TagType.COMPOUND, could abstract them away to not basically duplicate the same code twice
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null)
				return false

			if(!hasKey(tag, condition.nbtKey!!, condition.tagType.typeId))
				return false

			return if(TagType.isNumeric(condition.tagType))
				tag.getLong(condition.nbtKey) == (condition.value as Number).toLong()
			else
				when(condition.tagType) {
					TagType.BYTE_ARRAY -> tag.getByteArray(condition.nbtKey) contentEquals condition.value as ByteArray
					TagType.INT_ARRAY -> tag.getIntArray(condition.nbtKey) contentEquals condition.value as IntArray
					TagType.LONG_ARRAY -> tag.getLongArray(condition.nbtKey) contentEquals condition.value as LongArray
					TagType.LIST -> condition is NBTListCondition && tag.getTagList(condition.nbtKey, condition.listTagType.typeId).tagList == condition.value
					TagType.STRING -> tag.getString(condition.nbtKey).equals(condition.value as String)
					TagType.COMPOUND -> tag.getCompoundTag(condition.nbtKey).let { tag ->
						condition.value is NBTCondition && evaluate(tag, condition.value) || tag == condition.value
					}
					else -> throw IllegalStateException("TagType#isNumeric returned false on a numeric TagType")
				}
		}
	}

	/**
	 * Return true if the NBT tag is null, the condition is null, the condition's tag type is null, the NBT tag does not contain the key specified in the condition,
	 * or if the value associated with the key is the default value for its type (0 for numeric types, false for boolean, empty for arrays and strings).
	 */
	object NOT_PRESENT_OR_DEFAULT : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null)
				return true

			if(!hasKey(tag, condition.nbtKey!!, condition.tagType.typeId))
				return true

			return if(TagType.isNumeric(condition.tagType))
				tag.getLong(condition.nbtKey) == 0L
			else {
				when(condition.tagType) {
					TagType.BYTE_ARRAY -> tag.getByteArray(condition.nbtKey).isEmpty()
					TagType.INT_ARRAY -> tag.getIntArray(condition.nbtKey).isEmpty()
					TagType.LONG_ARRAY -> tag.getLongArray(condition.nbtKey).isEmpty()
					TagType.LIST -> condition is NBTListCondition && tag.getTagList(condition.nbtKey, condition.listTagType.typeId).isEmpty
					TagType.STRING -> tag.getString(condition.nbtKey).isEmpty()
					TagType.COMPOUND -> tag.getCompoundTag(condition.nbtKey).isEmpty
					else -> throw IllegalStateException("TagType#isNumeric returned false on a numeric TagType")
				}
			}
		}
	}

	object NOT_PRESENT_OR_HAS_KEY : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?) =
			tag == null || condition == null || condition.tagType == null || hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)
	}
}

