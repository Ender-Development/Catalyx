package org.ender_development.catalyx.core.recipes.maps

open class MapOreDictIngredient(var ore: Int) : AbstractMapIngredient() {
	override fun hash() =
		ore

	override fun equals(other: Any?) =
		this === other || (other is MapOreDictIngredient && ore == other.ore)
}
