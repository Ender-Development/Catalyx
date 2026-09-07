package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when
 * the current [net.minecraft.world.WorldProvider.MOON_PHASE_FACTORS] in in the [moonphases] list.
 */
class MoonCondition(val moonphases: List<Float>) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) =
		world.currentMoonPhaseFactorBody in moonphases.map { it.coerceIn(0.0F, 1.0F)}
	override fun toString() = "MOONPHASE:${moonphases.joinToString(",")}"
}
