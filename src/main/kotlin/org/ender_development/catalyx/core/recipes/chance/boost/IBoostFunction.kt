package org.ender_development.catalyx.core.recipes.chance.boost

/**
 * A function used to boost a [IBoost] chance based on the recipe tier and machine tier.
 */
@FunctionalInterface
fun interface IBoostFunction {
	/**
	 * Gets the boosted chance for a given boost, recipe tier, and machine tier.
	 *
	 * @param entry      The entry object containing the ingredient and its associated boost value.
	 * @param recipeTier The tier of the recipe being processed.
	 * @param machineTier The tier of the machine processing the recipe.
	 * @return The boosted chance as an integer.
	 */
	fun getBoostedChance(entry: IBoost<*>, recipeTier: Int, machineTier: Int): Int

	companion object {
		/**
		 * Predefined boost function that applies no boost.
		 */
		val NONE: IBoostFunction
			get() = IBoostFunction { entry, recipeTier, machineTier -> entry.chance }

		/**
		 * Predefined boost function that applies a boost based on the difference between machine tier and recipe
		 * tier. The boost is only applied if the machine tier is greater than the recipe tier.
		 * The boost is calculated as: `chance + (boost * (machineTier - recipeTier))`
		 */
		val TIER: IBoostFunction
			get() = IBoostFunction { entry, recipeTier, machineTier ->
				entry.chance + (entry.boost * (machineTier - recipeTier).coerceAtLeast(0))
			}
	}
}
