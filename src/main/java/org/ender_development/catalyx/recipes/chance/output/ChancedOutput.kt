package org.ender_development.catalyx.recipes.chance.output

import org.ender_development.catalyx.recipes.chance.ChancedBase

open class ChancedOutput<T> : ChancedBase<T> {
	constructor(ingredient: T, chance: Int) : super(ingredient, chance)

	override fun toString(): String {
		return "ChancedOutput{ingredient=${getIngredient()}, chance=${getChance()}}"
	}
}
