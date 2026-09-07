package org.ender_development.catalyx.api.v1.common.recipes.modifier

/**
 * A runtime container for all active [Modifier]s provided by the machine.
 *
 * Passed alongside a [org.ender_development.catalyx.api.v1.common.recipes.components.RecipeResult] to all [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler] query methods. The machine
 * is responsible for constructing and maintaining this - it should reflect the machine's
 * current state (installed upgrades, environmental buffs, etc.) at the time of each call.
 *
 * Multiplicative and additive modifier groups are resolved independently then multiplied
 * together. The combined result is clamped to each target's valid range.
 *
 * @property modifiers The list of active modifiers. Defaults to empty (no modification).
 */
class ModifierContext(val modifiers: List<Modifier> = emptyList()) {

	/**
	 * Resolves all modifiers for the given [target] into a single multiplier value.
	 *
	 * Returns 1.0 if no modifiers target the given [target].
	 *
	 * @param target The [ModifierTarget] to resolve.
	 * @return The final resolved multiplier after stacking and clamping.
	 */
	fun resolve(target: ModifierTarget): Double {
		val relevant = modifiers.filter { it.target == target }
		if (relevant.isEmpty()) return 1.0

		val multiplicative = relevant
			.filter { it.stackingMode == StackingMode.MULTIPLICATIVE }
			.fold(1.0) { acc, m -> acc * m.multiplier }

		val additive = relevant
			.filter { it.stackingMode == StackingMode.ADDITIVE }
			.fold(1.0) { acc, m -> acc + (m.multiplier - 1.0) }

		return when (target) {
			ModifierTarget.TIME           -> (multiplicative * additive).coerceAtLeast(0.0)
			ModifierTarget.ENERGY         -> (multiplicative * additive)
			ModifierTarget.CONSUME_CHANCE -> (multiplicative * additive).coerceIn(0.0, 1.0)
			ModifierTarget.OUTPUT_AMOUNT  -> (multiplicative * additive).coerceAtLeast(0.0)
			ModifierTarget.OUTPUT_CHANCE  -> (multiplicative * additive).coerceIn(0.01, 1.0)
		}
	}
}


