package org.ender_development.catalyx.api.v1.common.recipes.conditions

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Abstract base for all recipe conditions.
 *
 * A condition is a world/environment-based predicate that gates whether a recipe
 * is eligible to run. Conditions only interact with [World] - input-specific
 * matching logic belongs in the [org.ender_development.catalyx.api.v1.common.recipes.handlers.StackHandler] matching implementation.
 *
 * All implementations must provide a stable [toString] for use in recipe identity hashing.
 */
abstract class Condition {
	/**
	 * Evaluates this condition against the given [world] context.
	 *
	 * @param world The current [World] snapshot.
	 * @param pos The [BlockPos] of the machine.
	 * @return True if this condition is satisfied.
	 */
	abstract fun evaluate(world: World, pos: BlockPos): Boolean

	/**
	 * Returns a stable, deterministic canonical string representation.
	 * Used as part of the recipe identity hash. Must be consistent across JVM restarts.
	 */
	abstract override fun toString(): String
}
