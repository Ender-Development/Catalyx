package org.ender_development.catalyx.api.v1.common.recipes.conditions

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * A grouped set of [Condition]s with an associated [ConditionMode].
 *
 * Determines how the conditions are evaluated together and how they combine
 * with a parent [org.ender_development.catalyx.api.v1.common.recipes.RecipeMap]'s shared conditions.
 *
 * @property conditions The list of conditions in this set.
 * @property mode How conditions are combined and merged with map-level conditions.
 *   Defaults to [ConditionMode.AND].
 */
class ConditionSet(
	val conditions: List<Condition>,
	val mode: ConditionMode = ConditionMode.AND
) {
	/**
	 * Evaluates all conditions against the given [world] context.
	 *
	 * Returns false immediately if [world] is null - only condition-free recipes are
	 * eligible when no world context is available. Returns true if [conditions] is empty.
	 *
	 * @param world The current [World] snapshot, or null if unavailable.
	 * @param pos The current [BlockPos] where the machine is located, or null if unavailable.
	 */
	fun evaluate(world: World?, pos: BlockPos?): Boolean {
		if (world == null || pos == null) return false
		if (conditions.isEmpty()) return true
		return when (mode) {
			ConditionMode.REPLACE,
			ConditionMode.AND     -> conditions.all { it.evaluate(world, pos) }
			ConditionMode.OR      -> conditions.any { it.evaluate(world, pos) }
			ConditionMode.XOR     -> conditions.count { it.evaluate(world, pos) } and 1 == 1
		}
	}

	/**
	 * Merges this set with the given [mapConditions] from the parent [org.ender_development.catalyx.api.v1.common.recipes.RecipeMap].
	 *
	 * If [mode] is [ConditionMode.REPLACE] or [mapConditions] is null, returns this
	 * set unchanged - map conditions are ignored entirely. Otherwise, prepends the map's
	 * conditions and returns a new set with this set's [mode] applied to the combined list.
	 *
	 * @param mapConditions The shared conditions from the parent [org.ender_development.catalyx.api.v1.common.recipes.RecipeMap], or null.
	 */
	fun mergeWith(mapConditions: ConditionSet?): ConditionSet {
		if (mapConditions == null || mode == ConditionMode.REPLACE) return this
		return ConditionSet(mapConditions.conditions + conditions, mode)
	}

	override fun toString() =
		"CONDITIONS:$mode:[${conditions.sortedBy(Condition::toString).joinToString(",")}]"
}


