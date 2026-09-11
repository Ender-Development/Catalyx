package org.ender_development.catalyx.api.v1.common.recipes.modifier

/**
 * The value a [Modifier] targets when applied via a [ModifierContext].
 *
 * [TIME] affects the number of ticks a recipe takes to process. Floored at 1 tick after modifiers.
 * [ENERGY] affects the energy cost per tick. Can be negative, resulting in energy generation.
 * [CONSUME_CHANCE] affects the chance each input is consumed. Clamped to [0.0, 1.0].
 * [OUTPUT_AMOUNT] affects the amount of each output produced. Floored at 0 after modifiers.
 * [OUTPUT_CHANCE] affects the chance each output is produced. Clamped to [0.01, 1.0].
 */
enum class ModifierTarget {
	/** Affects the number of ticks a recipe takes to process. Floored at 1 tick after modifiers. */
	TIME,
	/** Affects the energy cost per tick. Can be negative, resulting in energy generation. */
	ENERGY,
	/** Affects the chance each input is consumed. Clamped to [0.0, 1.0]. */
	CONSUME_CHANCE,
	/** Affects the amount of each output produced. Floored at 0 after modifiers. */
	OUTPUT_AMOUNT,
	/** Affects the chance each output is produced. Clamped to [0.01, 1.0]. */
	OUTPUT_CHANCE
}
