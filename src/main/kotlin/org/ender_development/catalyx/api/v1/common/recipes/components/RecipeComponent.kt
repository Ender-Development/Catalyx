package org.ender_development.catalyx.api.v1.common.recipes.components


/**
 * Internal abstract base for all recipe inputs and outputs.
 *
 * End users never interact with this class directly - all recipe construction goes
 * through [org.ender_development.catalyx.api.v1.common.recipes.Recipe.Builder] using stacks, and all results are returned as typed stack
 * lists by [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler].
 *
 * All concrete implementations are data classes to support [plus] via [copy].
 *
 * @property amount The quantity of this resource required or produced.
 */
abstract class RecipeComponent {
	abstract val amount: Int

	/**
	 * Returns a stable, deterministic canonical string representation of this component.
	 * Used as part of the recipe identity hash. Must be consistent across JVM restarts.
	 * Collections must be sorted before joining to ensure order-independence.
	 */
	abstract override fun toString(): String
}
