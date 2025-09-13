package org.ender_development.catalyx.recipes.chance.output

import org.ender_development.catalyx.recipes.chance.boost.IBoost

open class BoostableChancedOutput<T> : ChancedOutput<T>, IBoost<T> {
	private val boost: Int

	constructor(ingredient: T, chance: Int, boost: Int) : super(ingredient, chance) {
		this.boost = boost
	}

	override fun getBoost(): Int {
		return  boost
	}

	override fun toString(): String {
		return "BoostableChancedOutput{ingredient=${getIngredient()}, chance=${getChance()}, boost=$boost}"
	}
}
