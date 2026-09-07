package org.ender_development.catalyx.api.v1.common.recipes.components.impl

import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.api.v1.common.extensions.nbtString
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode

/**
 * Internal [RecipeOutput] implementation for fluid-type resources.
 *
 * @property amount The base amount of fluid to produce in millibuckets. Must be > 0.
 * @property fluid The id of the fluid to produce.
 * @property produceChance The probability this output is produced. Must be in [0.01, 1.0].
 * @property rollMode How produce chance is rolled. Defaults to [RollMode.PER_STACK].
 */
data class FluidRecipeOutput(
	override val amount: Int,
	val fluid: FluidStack,
	override val produceChance: Double = 1.0,
	override val rollMode: RollMode = RollMode.PER_STACK
) : RecipeOutput() {

	init {
		require(amount > 0) { "FluidRecipeOutput amount must be > 0" }
		require(produceChance in 0.01..1.0) { "produceChance must be in [0.01, 1.0]" }
	}

	/**
	 * Returns a copy of this output with [other]'s amount added.
	 * Used to combine multiple outputs of the same fluid type after chance resolution.
	 *
	 * @throws IllegalArgumentException if [other] is not a [FluidRecipeOutput].
	 */
	override operator fun plus(other: RecipeOutput): FluidRecipeOutput {
		require(other is FluidRecipeOutput) {
			"Cannot add FluidRecipeOutput and ${other::class.simpleName}"
		}
		return copy(amount = amount + other.amount)
	}

	override fun toString() =
		"FLUID:${fluid.nbtString()}:$amount:$produceChance:$rollMode"
}


