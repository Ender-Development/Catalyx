package org.ender_development.catalyx.api.v1.common.recipes.components

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.Recipe
import org.ender_development.catalyx.api.v1.common.recipes.RecipeMap

/**
 * An immutable snapshot of a successful recipe lookup.
 *
 * Returned by [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler.getRecipeFromStacks] and passed to all subsequent
 * handler calls. Carries everything the recipe system needs internally to resolve
 * time, energy, outputs, and consumption - the machine never needs to inspect
 * its contents directly.
 *
 * Since this is a point-in-time snapshot, if the world state changes significantly
 * mid-process (e.g. a condition is no longer met), the machine should decide whether
 * to re-query, pause, or abort.
 *
 * @property world The [World] snapshot captured at the time of the lookup.
 *   May be null if no world was provided, in which case only condition-free
 *   recipes were considered.
 * @property pos The [BlockPos] the machine querying the recipe is placed at.
 */
data class RecipeResult(
	internal val recipe: Recipe,
	internal val map: RecipeMap,
	val world: World?,
	val pos: BlockPos?
)


