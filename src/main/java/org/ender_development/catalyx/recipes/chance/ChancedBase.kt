package org.ender_development.catalyx.recipes.chance

open class ChancedBase<T> : IChance<T> {
	private val ingredient: T
	private val chance: Int

	constructor(ingredient: T, chance: Int) {
		this.ingredient = ingredient
		this.chance = chance
	}

	override fun getIngredient(): T {
		return ingredient
	}

	override fun getChance(): Int {
		return chance
	}

	override fun copy(): IChance<T> {
		return ChancedBase(ingredient, chance)
	}

	override fun toString(): String {
		return "ChancedBase{ingredient=$ingredient, chance=$chance}"
	}
}
