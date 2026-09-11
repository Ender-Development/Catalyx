package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when
 * the light-level around the machine is higher than the provided [value].
 *
 * @param value The minimal light-level required. (non-inclusive)
 */
class BrightnessCondition(val value: Int) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.getLightFromNeighbors(pos) > value
	override fun toString() = "LIGHT:BrighterThan:$value"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when
 * the light-level around the machine is lower than the provided [value].
 *
 * @param value The maximal light-level required. (non-inclusive)
 */
class DarknessCondition(val value: Int) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.getLightFromNeighbors(pos) < value
	override fun toString() = "LIGHT:DarkerThan:$value"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when
 * the machine can see the sky.
 */
class SkyCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.canBlockSeeSky(pos)
	override fun toString() = "LIGHT:Sky"
}
