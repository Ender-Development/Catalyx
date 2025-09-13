package org.ender_development.catalyx.recipes.ingredients.nbt

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagLongArray
import net.minecraftforge.fluids.FluidStack

interface IMatcher {
	companion object {
		fun hasKey(tag: NBTTagCompound?, key: String, tagType: Int) = tag?.hasKey(key, tagType) == true
	}

	fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean

	fun evaluate(stack: ItemStack, condition: NBTCondition?): Boolean = evaluate(stack.tagCompound, condition)

	fun evaluate(stack: FluidStack, condition: NBTCondition?): Boolean = evaluate(stack.tag, condition)

	/**
	 * Return true without checking if the NBT tags actually match or even exist.
	 */
	object ANY : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?) = true
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is less than the value specified in the condition.
	 */
	object LESS_THAN : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				if(TagType.isNumeric(condition.tagType)) {
					return tag!!.getLong(condition.nbtKey) < (condition.value as Number).toLong()
				}
			}
			return false
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is less than or equal to the value specified in the condition.
	 */
	object LESS_THAN_OR_EQUAL : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				if(TagType.isNumeric(condition.tagType)) {
					return tag!!.getLong(condition.nbtKey) <= (condition.value as Number).toLong()
				}
			}
			return false
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is greater than the value specified in the condition.
	 */
	object GREATER_THAN : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				if(TagType.isNumeric(condition.tagType)) {
					return tag!!.getLong(condition.nbtKey) > (condition.value as Number).toLong()
				}
			}
			return false
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is greater than or equal to the value specified in the condition.
	 */
	object GREATER_THAN_OR_EQUAL : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				if(TagType.isNumeric(condition.tagType)) {
					return tag!!.getLong(condition.nbtKey) >= (condition.value as Number).toLong()
				}
			}
			return false
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is equal to the value specified in the condition.
	 */
	object EQUAL_TO : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				return if(TagType.isNumeric(condition.tagType)) {
					tag!!.getLong(condition.nbtKey) == (condition.value as Number).toLong()
				} else {
					when(condition.tagType) {
						TagType.BOOLEAN -> tag!!.getBoolean(condition.nbtKey) == (condition.value as Boolean)
						TagType.BYTE_ARRAY -> tag!!.getByteArray(condition.nbtKey) contentEquals (condition.value as ByteArray)
						TagType.STRING -> tag!!.getString(condition.nbtKey).equals(condition.value as String)
						TagType.COMPOUND -> tag!!.getCompoundTag(condition.nbtKey).equals(condition.value as NBTTagCompound)
						TagType.INT_ARRAY -> tag!!.getIntArray(condition.nbtKey) contentEquals (condition.value as IntArray)
						TagType.LONG_ARRAY -> (tag!!.getTag(condition.nbtKey) as NBTTagLongArray).data contentEquals (condition.value as LongArray)
						TagType.LIST -> if(condition is NBTListCondition) tag!!.getTagList(condition.nbtKey, condition.listTagType!!.typeId).tagList.equals(condition.value) else false
						else -> false
					}
				}
			}
			return false
		}
	}

	/**
	 * Check if the NBT tag contains the key specified in the condition and if its value is equal to the value specified in the condition.
	 * If the value is a compound tag, it will recursively check if all keys and values match.
	 */
	object RECURSIVE_EQUAL_TO : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(condition == null || condition.tagType == null) return false
			if(hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) {
				return if(TagType.isNumeric(condition.tagType)) {
					tag!!.getLong(condition.nbtKey) == (condition.value as Number).toLong()
				} else {
					when(condition.tagType) {
						TagType.BOOLEAN -> tag!!.getBoolean(condition.nbtKey) == (condition.value as Boolean)
						TagType.BYTE_ARRAY -> tag!!.getByteArray(condition.nbtKey) contentEquals (condition.value as ByteArray)
						TagType.STRING -> tag!!.getString(condition.nbtKey).equals(condition.value as String)
						TagType.COMPOUND -> if (condition.value is NBTCondition) evaluate(tag!!.getCompoundTag(condition.nbtKey), condition.value) else tag!!.getCompoundTag(condition.nbtKey).equals(condition.value as NBTTagCompound)
						TagType.INT_ARRAY -> tag!!.getIntArray(condition.nbtKey) contentEquals (condition.value as IntArray)
						TagType.LONG_ARRAY -> (tag!!.getTag(condition.nbtKey) as NBTTagLongArray).data contentEquals (condition.value as LongArray)
						TagType.LIST -> if(condition is NBTListCondition) tag!!.getTagList(condition.nbtKey, condition.listTagType!!.typeId).tagList.equals(condition.value) else false
						else -> false
					}
				}
			}
			return false
		}
	}

	/**
	 * Return true if the NBT tag is null, the condition is null, the condition's tag type is null, the NBT tag does not contain the key specified in the condition,
	 * or if the value associated with the key is the default value for its type (0 for numeric types, false for boolean, empty for arrays and strings).
	 */
	object NOT_PRESENT_OR_DEFAULT : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null) return true
			if(!hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)) return true
			return if(TagType.isNumeric(condition.tagType)) {
				tag.getLong(condition.nbtKey) == 0L
			} else {
				when(condition.tagType) {
					TagType.BOOLEAN -> !tag.getBoolean(condition.nbtKey)
					TagType.BYTE_ARRAY -> tag.getByteArray(condition.nbtKey).isEmpty()
					TagType.STRING -> tag.getString(condition.nbtKey).isEmpty()
					TagType.COMPOUND -> tag.getCompoundTag(condition.nbtKey).isEmpty
					TagType.INT_ARRAY -> tag.getIntArray(condition.nbtKey).isEmpty()
					TagType.LONG_ARRAY -> (tag.getTag(condition.nbtKey) as NBTTagLongArray).data.isEmpty()
					TagType.LIST -> if(condition is NBTListCondition) tag.getTagList(condition.nbtKey, condition.listTagType!!.typeId).isEmpty else false
					else -> false
				}
			}
		}
	}

	object NOT_PRESENT_OR_HAS_KEY : IMatcher {
		override fun evaluate(tag: NBTTagCompound?, condition: NBTCondition?): Boolean {
			if(tag == null || condition == null || condition.tagType == null) return true
			return hasKey(tag, condition.nbtKey!!, condition.tagType.typeId)
		}
	}
}

