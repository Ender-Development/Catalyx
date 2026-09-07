package org.ender_development.catalyx.api.v1.common.recipes.conditions.impl

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition

/**
 * A [org.ender_development.catalyx.api.v1.common.recipes.conditions.Condition] satisfied when the machine is in one of the specified biomes.
 *
 * @property biomes The list of biome ids that satisfy this condition.
 */
class BiomeCondition(val biomes: List<String>) : Condition() {
	override fun evaluate(world: World, pos: BlockPos) = world.getBiome(pos).biomeName in biomes
	override fun toString() = "BIOME:[${biomes.sorted().joinToString(",")}]"
}


