package org.ender_development.catalyx.core.tiles

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.blocks.multiblock.IMultiblockEdge
import org.ender_development.catalyx.core.blocks.multiblock.IMultiblockTile
import org.ender_development.catalyx.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx.core.utils.DevUtils
import org.ender_development.catalyx.core.utils.extensions.getHorizontalSurroundings

open class CenterTile(mod: ICatalyxMod) : BaseTile(mod), IMultiblockTile {
	internal constructor() : this(Catalyx) {
		if(!DevUtils.isDeobfuscated)
			error("use the full constructor")
	}

	override fun activate(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Double, hitY: Double, hitZ: Double) =
		!player.isSneaking

	override fun breakBlock(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		pos.getHorizontalSurroundings().forEach {
			if(world.getBlockState(it).block is IMultiblockEdge)
				world.destroyBlock(it, false)
		}
	}
}
