package org.ender_development.catalyx.core.recipes.chance

open class ChancedBase<T>(override val ingredient: T, override val chance: Int) : IChance<T> {
	override fun copy() =
		ChancedBase(ingredient, chance)

	override fun toString() =
		"ChancedBase{ingredient=$ingredient, chance=$chance}"
}
