package org.ender_development.catalyx.core.recipes.chance.output

import org.ender_development.catalyx.core.recipes.chance.boost.IBoostFunction

class ChancedOutputList<I, T : ChancedOutput<I>>(val chancedOutputLogic: IChancedOutputLogic, val chancedElements: List<T>) {
	companion object {
		fun <I, T : ChancedOutput<I>> empty(): ChancedOutputList<I, T> =
			ChancedOutputList(IChancedOutputLogic.NONE, emptyList())
	}

	/**
	 * Rolls the chanced output list using the provided boost function, recipe tier, and machine tier.
	 *
	 * @param boostFunction The boost function to apply during the roll.
	 * @param recipeTier The tier of the recipe being processed.
	 * @param machineTier The tier of the machine processing the recipe.
	 * @return A list of rolled ingredients of type T.
	 */
	fun roll(boostFunction: IBoostFunction, recipeTier: Int, machineTier: Int) =
		chancedOutputLogic.roll(chancedElements, boostFunction, recipeTier, machineTier)

	override fun toString() =
		chancedElements.toString()
}
