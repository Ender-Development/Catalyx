package org.ender_development.catalyx.recipes.maps

import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx.recipes.ingredients.nbt.IMatcher
import org.ender_development.catalyx.recipes.ingredients.nbt.NBTCondition

class MapOreDictNBTIngredient : MapOreDictIngredient {
	var condition: NBTCondition? = null
	var matcher: IMatcher? = null
	var nbtTagCompound: NBTTagCompound? = null

	constructor(ore: Int, matcher: IMatcher?, condition: NBTCondition?) : super(ore) {
		this.matcher = matcher
		this.condition = condition
	}

	constructor(ore: Int, nbtTagCompound: NBTTagCompound?) : super(ore) {
		this.nbtTagCompound = nbtTagCompound
	}

	override fun equals(other: Any?): Boolean {
		if(this === other) {
			return true
		}
		if(other is MapOreDictNBTIngredient) {
			val other = other
			if(this.matcher != null && other.matcher != null) {
				if(this.matcher!! != other.matcher) {
					return false
				}
			}
			if(this.condition != null && other.condition != null) {
				if(!this.condition!!.equals(other.condition)) {
					return false
				}
			}
			if(ore == other.ore) {
				return other.matcher?.evaluate(this.nbtTagCompound, other.condition) ?: false
			}
		}
		return false
	}

	override val isSpecialIngredient: Boolean = true
}
