package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is daytime.
 */
class DayCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.isDaytime
	override fun toString() = "TIME:isDaytime"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is nighttime.
 */
class NightCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = !world.isDaytime
	override fun toString() = "TIME:isNighttime"
}
