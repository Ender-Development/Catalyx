package org.ender_development.catalyx_.core.blocks.multiblock.parts

import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx_.modules.coremodule.ICatalyxMod

open class InvisibleCorner(mod: ICatalyxMod, name: String) : CornerBlock(mod, name) {
	init {
	    translucent = true
	}

	override fun getRenderLayer(): BlockRenderLayer =
		BlockRenderLayer.TRANSLUCENT

	@Deprecated("Implementation is fine.")
	override fun getRenderType(state: IBlockState): EnumBlockRenderType =
		EnumBlockRenderType.INVISIBLE

	@Deprecated("Implementation is fine.")
	override fun isOpaqueCube(state: IBlockState) =
		false

	@SideOnly(Side.CLIENT)
	@Deprecated("Implementation is fine.")
	override fun getAmbientOcclusionLightValue(state: IBlockState): Float =
		1.0f
}

open class InvisibleSide(mod: ICatalyxMod, name: String) : SideBlock(mod, name) {
	init {
	    translucent = true
	}

	override fun getRenderLayer(): BlockRenderLayer =
		BlockRenderLayer.TRANSLUCENT

	@Deprecated("Implementation is fine.")
	override fun getRenderType(state: IBlockState): EnumBlockRenderType =
		EnumBlockRenderType.INVISIBLE

	@Deprecated("Implementation is fine.")
	override fun isOpaqueCube(state: IBlockState) =
		false

	@SideOnly(Side.CLIENT)
	@Deprecated("Implementation is fine.")
	override fun getAmbientOcclusionLightValue(state: IBlockState): Float =
		1.0f
}
