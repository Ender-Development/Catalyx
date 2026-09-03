package org.ender_development.catalyx.modules.integration.crafttweaker

import crafttweaker.annotations.ZenRegister
import crafttweaker.api.block.IMaterial
import crafttweaker.api.minecraft.CraftTweakerMC
import net.minecraft.block.Block
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.common.blocks.base.CatBlock
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@Suppress("UNUSED")
@ZenRegister
@ZenClass("${Reference.MODID}.content.Block")
class CatalyxBlock(private val block: Block) {
	companion object {
		private fun init(block: Block, name: String): CatalyxBlock =
			CatalyxBlock(block.setRegistryName(Reference.MODID, name).setTranslationKey("tile.${Reference.MODID}:$name.name"))

		@ZenMethod
		fun createBlock(name: String, material: IMaterial): CatalyxBlock =
			init(CatBlock(Catalyx, name, CraftTweakerMC.getMaterial(material)), name)
	}

	@ZenMethod
	fun setHardness(hardness: Float): CatalyxBlock {
		block.setHardness(hardness)
		return this
	}

	@ZenMethod
	fun setResistance(resistance: Float): CatalyxBlock {
		block.setResistance(resistance)
		return this
	}

	@ZenMethod
	fun setUnbreakable(): CatalyxBlock {
		block.setBlockUnbreakable()
		return this
	}

	@ZenMethod
	fun setLightOpacity(opacity: Int): CatalyxBlock {
		block.setLightOpacity(opacity)
		return this
	}

	@ZenMethod
	fun setLightLevel(light: Float): CatalyxBlock {
		block.setLightLevel(light)
		return this
	}

	@ZenMethod
	fun setSlipperiness(slipperiness: Float): CatalyxBlock {
		block.setDefaultSlipperiness(slipperiness)
		return this
	}

	@ZenMethod
	fun setHarvestLevel(tool: String, level: Int): CatalyxBlock {
		block.setHarvestLevel(tool, level)
		return this
	}
}
