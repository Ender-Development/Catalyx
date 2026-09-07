package org.ender_development.catalyx.api.v1.common.recipes.modifier

/**
 * Abstract base for all recipe modifiers.
 *
 * Represents a runtime multiplier applied to a specific [ModifierTarget] during recipe
 * processing. Provided by the machine via [ModifierContext] - never part of a recipe
 * definition itself.
 *
 * Modifiers are expressed as multipliers: 1.0 = no change, 1.2 = +20%, 0.8 = -20%.
 *
 * @property target The [ModifierTarget] this modifier applies to.
 * @property multiplier The multiplier value.
 * @property stackingMode How this modifier combines with others targeting the same value.
 *   Defaults to [StackingMode.MULTIPLICATIVE].
 */
abstract class Modifier {
	abstract val target: ModifierTarget
	abstract val multiplier: Double
	abstract val stackingMode: StackingMode
	abstract override fun toString(): String
}
