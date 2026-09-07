package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is raining.
 */
class RainCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.isRainingAt(pos)
	override fun toString() = "WEATHER:isRain"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is thundering.
 */
class ThunderCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.isThundering
	override fun toString() = "WEATHER:isThunder"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is snowing.
 */
class SnowCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.canSnowAt(pos, false) && world.isRainingAt(pos)
	override fun toString() = "WEATHER:isSnow"
}

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when it is sunny.
 */
class SunnyCondition : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = !world.isThundering && !world.isRainingAt(pos)
	override fun toString() = "WEATHER:isSunny"
}
