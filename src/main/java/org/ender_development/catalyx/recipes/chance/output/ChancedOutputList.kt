package org.ender_development.catalyx.recipes.chance.output

import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction

class ChancedOutputList<I, T: ChancedOutput<I>> {
	private val chancedOutputLogic: IChancedOutputLogic
	private val chancedElements: List<T>

	constructor(chancedOutputLogic: IChancedOutputLogic, chancedElements: List<T>) {
		this.chancedOutputLogic = chancedOutputLogic
		this.chancedElements = chancedElements
	}

	companion object {
		fun <I, T: ChancedOutput<I>> empty(): ChancedOutputList<I, T> {
			return ChancedOutputList(IChancedOutputLogic.NONE, emptyList())
		}
	}

	fun getChancedElements(): List<T> = chancedElements

	fun getChancedOutputLogic(): IChancedOutputLogic = chancedOutputLogic

	/**
	 * Rolls the chanced output list using the provided boost function, recipe tier, and machine tier.
	 *
	 * @param boostFunction The boost function to apply during the roll.
	 * @param recipeTier The tier of the recipe being processed.
	 * @param machineTier The tier of the machine processing the recipe.
	 * @return A list of rolled ingredients of type T.
	 */
	fun roll(boostFunction: IBoostFunction, recipeTier: Int, machineTier: Int): List<T>? = chancedOutputLogic.roll(getChancedElements(), boostFunction, recipeTier, machineTier)

	override fun toString(): String = chancedElements.toString()
}
