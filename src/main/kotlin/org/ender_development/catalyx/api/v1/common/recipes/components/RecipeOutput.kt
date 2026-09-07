package org.ender_development.catalyx.api.v1.common.recipes.components

/**
 * Internal abstract base for all recipe output definitions.
 *
 * @property produceChance The probability this output is produced when the recipe completes.
 *   Clamped to [0.01, 1.0]. Defaults to 1.0.
 * @property rollMode Whether chance is rolled once per stack or once per item.
 *   Defaults to [RollMode.PER_STACK].
 */
abstract class RecipeOutput : RecipeComponent() {
	abstract val produceChance: Double
	abstract val rollMode: RollMode

	/**
	 * Returns a copy of this component with [other]'s amount added to this component's amount.
	 * Used to aggregate multiple stacks of the same resource type before recipe matching.
	 *
	 * Implementations should require that [other] is the same concrete type.
	 *
	 * @param other The component to add. Must be the same concrete type as this component.
	 * @throws IllegalArgumentException if [other] is not the same concrete type.
	 */
	abstract operator fun plus(other: RecipeOutput): RecipeOutput
}
