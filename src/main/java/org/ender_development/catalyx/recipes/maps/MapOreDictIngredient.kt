package org.ender_development.catalyx.recipes.maps

open class MapOreDictIngredient(var ore: Int) : AbstractMapIngredient() {
	override fun hash(): Int {
		return ore
	}

	override fun equals(other: Any?): Boolean {
		if(super.equals(other)) {
			return ore == (other as MapOreDictIngredient).ore
		}
		return false
	}
}
