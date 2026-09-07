package org.ender_development.catalyx.api.v1.common.recipes.components

/**
 * Internal abstract base for all recipe input definitions.
 *
 * @property consumeChance The probability this input is consumed when the recipe runs.
 *   Clamped to [0.0, 1.0]. A value of 0.0 marks this as a catalyst -
 *   present but never consumed. Defaults to 1.0.
 * @property rollMode Whether chance is rolled once per stack or once per item.
 *   Defaults to [RollMode.PER_STACK].
 */
abstract class RecipeInput : RecipeComponent() {
	abstract val consumeChance: Double
	abstract val rollMode: RollMode

	/**
	 * Returns true if the given [provided] component satisfies this input requirement.
	 *
	 * Implementations should verify:
	 * - The provided component is the correct concrete type
	 * - The provided resource matches one of the accepted variants
	 * - The provided amount is >= the required [amount]
	 */
	abstract fun matches(provided: RecipeComponent): Boolean

	/**
	 * Returns a copy of this component with [other]'s amount added to this component's amount.
	 * Used to aggregate multiple stacks of the same resource type before recipe matching.
	 *
	 * Implementations should require that [other] is the same concrete type.
	 *
	 * @param other The component to add. Must be the same concrete type as this component.
	 * @throws IllegalArgumentException if [other] is not the same concrete type.
	 */
	abstract operator fun plus(other: RecipeInput): RecipeInput
}




