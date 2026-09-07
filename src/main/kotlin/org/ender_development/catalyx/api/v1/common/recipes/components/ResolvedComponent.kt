package org.ender_development.catalyx.api.v1.common.recipes.components

/**
 * Internal result of resolving a single [RecipeInput] after modifiers and chance rolls.
 * Never exposed to the machine - consumed internally by [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler] stack helpers.
 *
 * @property component The original input definition.
 * @property amountToConsume The final amount to remove from the machine's inventory.
 *   May be 0 for catalysts or failed chance rolls - recipe still runs in both cases.
 */
internal data class ResolvedInput(
	val component: RecipeInput,
	val amountToConsume: Int
)

/**
 * Internal result of resolving a single [RecipeOutput] after modifiers and chance rolls.
 * Never exposed to the machine - consumed internally by [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler] stack helpers.
 *
 * @property component The original output definition.
 * @property amount The final amount to produce. May be 0 if chance roll failed.
 */
internal data class ResolvedOutput(
	val component: RecipeOutput,
	val amount: Int
)

