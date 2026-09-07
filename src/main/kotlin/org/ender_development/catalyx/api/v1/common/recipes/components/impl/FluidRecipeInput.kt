package org.ender_development.catalyx.api.v1.common.recipes.components.impl

import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.api.v1.common.extensions.nbtString
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeInput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode

/**
 * Internal [RecipeInput] implementation for fluid-type resources.
 *
 * @property amount The amount of fluid required in millibuckets. Must be > 0.
 * @property validFluids The list of fluid ids that satisfy this input. Must not be empty.
 * @property consumeChance The probability this input is consumed. Defaults to 1.0.
 * @property rollMode How consume chance is rolled. Defaults to [RollMode.PER_STACK].
 */
data class FluidRecipeInput(
	override val amount: Int,
	val validFluids: List<FluidStack>,
	override val consumeChance: Double = 1.0,
	override val rollMode: RollMode = RollMode.PER_STACK
) : RecipeInput() {

	init {
		require(amount > 0) { "FluidRecipeInput amount must be > 0" }
		require(consumeChance in 0.0..1.0) { "consumeChance must be in [0.0, 1.0]" }
		require(validFluids.isNotEmpty()) { "FluidRecipeInput must have at least one valid fluid" }
	}

	/**
	 * Returns true if [provided] is a [FluidRecipeInput] whose fluid is in [validFluids]
	 * and whose amount is >= the required [amount].
	 */
	override fun matches(provided: RecipeComponent): Boolean =
		provided is FluidRecipeInput && validFluids.any {
			// TODO: This is totally winged, pls check if this works. :c
			provided.validFluids.any { other ->
				if (it.fluid != other.fluid) return false
				(it.tag == null || it.tag == other.tag)
			}
		} && provided.amount >= amount

	/**
	 * Returns a copy of this input with [other]'s amount added.
	 * Used to aggregate multiple fluid stacks of the same type before recipe matching.
	 *
	 * @throws IllegalArgumentException if [other] is not a [FluidRecipeInput].
	 */
	override operator fun plus(other: RecipeInput): FluidRecipeInput {
		require(other is FluidRecipeInput) {
			"Cannot add FluidRecipeInput and ${other::class.simpleName}"
		}
		return copy(amount = amount + other.amount)
	}

	override fun toString() =
		"FLUID:[${
			validFluids.map {
				it.nbtString()
			}.sorted().joinToString(",")
		}]:$amount:$consumeChance:$rollMode"
}


