package org.ender_development.catalyx.tiles

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.multiblock.BaseEdge
import org.ender_development.catalyx.blocks.multiblock.IMultiBlockPart
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.utils.extensions.getHorizontalSurroundings

open class BaseMiddleTile(override val mod: ICatalyxMod) : BaseTile(mod), IMultiBlockPart {
	override fun activate(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Double, hitY: Double, hitZ: Double) = false

	override fun breakBlock(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		pos.getHorizontalSurroundings().forEach {
			if(world.getBlockState(it).block is BaseEdge)
				world.setBlockToAir(it)
		}
	}
}
