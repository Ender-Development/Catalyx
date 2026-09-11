package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when
 * the current [net.minecraft.world.WorldProvider.MOON_PHASE_FACTORS] in in the [moonPhases] list.
 */
class MoonCondition(val moonPhases: List<Float>) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) =
		world.currentMoonPhaseFactorBody in moonPhases.map { it.coerceIn(0f, 1f)}
	override fun toString() = "MOONPHASE:${moonPhases.joinToString(",")}"
}
