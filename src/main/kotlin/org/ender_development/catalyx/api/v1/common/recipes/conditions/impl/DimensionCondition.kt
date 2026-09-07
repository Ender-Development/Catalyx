package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when the machine is in one of the specified dimensions.
 *
 * @property dimensions The list of dimension names that satisfy this condition.
 */
class DimensionCondition(val dimensions: List<String>) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.provider.dimensionType.name in dimensions
	override fun toString() = "DIMENSION:[${dimensions.sorted().joinToString(",")}]"
}


