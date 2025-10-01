package org.ender_development.catalyx.blocks

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.core.CatalyxSettings

/**
 * A base Catalyx Block
 */
open class BaseBlock(settings: CatalyxSettings, name: String, material: Material = Material.ROCK, hardness: Float = 3f) : AbstractBlock(settings, material) {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		blockHardness = hardness
		settings.blocks(this)
	}
}
