package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import org.ender_development.catalyx.core.ICatalyxMod

open class InvisibleCorner(mod: ICatalyxMod, name: String) : CornerBlock(mod, name) {
	init {
	    translucent = true
	}

	override fun getRenderLayer(): BlockRenderLayer {
		return BlockRenderLayer.TRANSLUCENT
	}

	@Deprecated("Implementation is fine.")
	override fun getRenderType(state: IBlockState): EnumBlockRenderType =
		EnumBlockRenderType.INVISIBLE

	@Deprecated("Implementation is fine.")
	override fun isOpaqueCube(state: IBlockState) =
		false
}

open class InvisibleSide(mod: ICatalyxMod, name: String) : SideBlock(mod, name) {
	init {
	    translucent = true
	}

	override fun getRenderLayer(): BlockRenderLayer {
		return BlockRenderLayer.TRANSLUCENT
	}

	@Deprecated("Implementation is fine.")
	override fun getRenderType(state: IBlockState): EnumBlockRenderType =
		EnumBlockRenderType.INVISIBLE

	@Deprecated("Implementation is fine.")
	override fun isOpaqueCube(state: IBlockState) =
		false
}
