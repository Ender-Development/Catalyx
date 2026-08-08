package org.ender_development.catalyx.core.common.blocks.base

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.registry.IBlockProvider
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.register

/**
 * The most minimal version of a block class utilizing features. We use this for the custom blocks that
 * that can be created using the integration with CraftTweaker and GroovyScript.
 */
open class CatBlock(val mod: ICatalyxMod, name: String, material: Material): Block(material), IBlockProvider {
	init {
		registryName = ResourceLocation(mod.modId, name)
	    translationKey = "${mod.modId}:$name"
		creativeTab = mod.creativeTab
	}

	override val instance: Block = this

	init {
	    mod.register(this)
	}
}
