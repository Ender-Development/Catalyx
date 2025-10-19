package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IMultiBlockPart {
	fun activate(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Double, hitY: Double, hitZ: Double): Boolean

	fun breakBlock(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer)
}
