package org.ender_development.catalyx.api.v1.common.recipes.modifier

/**
 * Determines how multiple [Modifier]s targeting the same [ModifierTarget] are combined.
 *
 * [MULTIPLICATIVE] multiplies all modifiers together: base * m1 * m2 * ...
 * [ADDITIVE] treats each modifier as a delta from 1.0 and sums them: base * (m1 + m2 - 1.0 + ...)
 *
 * When both stacking modes are present for the same target, each group is resolved independently
 * and then the two results are multiplied together.
 */
enum class StackingMode {
	/** Multiplies all modifiers together: base * m1 * m2 * ... */
	MULTIPLICATIVE,
	/** Treats each modifier as a delta from 1.0 and sums them: base * (m1 + m2 - 1.0 + ...) */
	ADDITIVE
}
