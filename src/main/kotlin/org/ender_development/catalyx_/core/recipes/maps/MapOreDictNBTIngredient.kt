package org.ender_development.catalyx_.core.recipes.maps

import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx_.core.recipes.ingredients.nbt.IMatcher
import org.ender_development.catalyx_.core.recipes.ingredients.nbt.NBTCondition

class MapOreDictNBTIngredient : MapOreDictIngredient {
	val matcher: IMatcher?
	val condition: NBTCondition?
	val nbtTagCompound: NBTTagCompound?

	constructor(ore: Int, matcher: IMatcher?, condition: NBTCondition?) : super(ore) {
		this.matcher = matcher
		this.condition = condition
		nbtTagCompound = null
	}

	constructor(ore: Int, nbtTagCompound: NBTTagCompound?) : super(ore) {
		matcher = null
		condition = null
		this.nbtTagCompound = nbtTagCompound
	}

	override fun equals(other: Any?) =
		this === other || (other is MapOreDictNBTIngredient && matcher == other.matcher && condition == other.condition && ore == other.ore && other.matcher?.evaluate(nbtTagCompound, other.condition) == true)

	override val isSpecialIngredient = true
}
