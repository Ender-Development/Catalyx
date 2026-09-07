package org.ender_development.catalyx.api.v1.common.recipes.modifier

/**
 * Affects recipe processing time. Resolved value is floored at 1 tick.
 * Values < 1.0 speed up processing.
 */
class TimeModifier(
	override val multiplier: Double,
	override val stackingMode: StackingMode = StackingMode.MULTIPLICATIVE
) : Modifier() {
	override val target = ModifierTarget.TIME
	override fun toString() = "TIME:$multiplier:$stackingMode"
}

/**
 * Affects energy cost per tick. Uncapped - negative base energy produces generation.
 */
class EnergyModifier(
	override val multiplier: Double,
	override val stackingMode: StackingMode = StackingMode.MULTIPLICATIVE
) : Modifier() {
	override val target = ModifierTarget.ENERGY
	override fun toString() = "ENERGY:$multiplier:$stackingMode"
}

/**
 * Affects the consume chance of all inputs. Clamped to [0.0, 1.0].
 * Catalyst inputs (consumeChance == 0.0) are never affected.
 */
class ConsumeChanceModifier(
	override val multiplier: Double,
	override val stackingMode: StackingMode = StackingMode.MULTIPLICATIVE
) : Modifier() {
	override val target = ModifierTarget.CONSUME_CHANCE
	override fun toString() = "CONSUME_CHANCE:$multiplier:$stackingMode"
}

/**
 * Affects the output amount of all outputs. Floored at 0.
 * Values > 1.0 increase yield.
 */
class OutputAmountModifier(
	override val multiplier: Double,
	override val stackingMode: StackingMode = StackingMode.MULTIPLICATIVE
) : Modifier() {
	override val target = ModifierTarget.OUTPUT_AMOUNT
	override fun toString() = "OUTPUT_AMOUNT:$multiplier:$stackingMode"
}

/**
 * Affects the produce chance of all outputs. Clamped to [0.01, 1.0].
 * Outputs can never be fully suppressed via chance modifiers.
 */
class OutputChanceModifier(
	override val multiplier: Double,
	override val stackingMode: StackingMode = StackingMode.MULTIPLICATIVE
) : Modifier() {
	override val target = ModifierTarget.OUTPUT_CHANCE
	override fun toString() = "OUTPUT_CHANCE:$multiplier:$stackingMode"
}

