package org.ender_development.catalyx.blocks.multiblock.parts

import net.minecraft.block.Block.FULL_BLOCK_AABB
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.multiblock.Facing
import org.ender_development.catalyx.blocks.multiblock.Facing.Companion.binary
import org.ender_development.catalyx.blocks.multiblock.Facing.Companion.opposite
import org.ender_development.catalyx.blocks.multiblock.Position
import org.ender_development.catalyx.blocks.multiblock.with
import org.ender_development.catalyx.core.ICatalyxMod

open class CornerBlock(mod: ICatalyxMod, name: String) : AbstractEdgeBlock(mod, name) {
	override fun getCenter(pos: BlockPos, state: IBlockState): BlockPos {
		/*
		EnumFacing.NORTH with Position.P0, EnumFacing.EAST with Position.P1, EnumFacing.SOUTH with Position.P2, EnumFacing.WEST with Position.P3 -> pos.north().east()
		EnumFacing.NORTH with Position.P1, EnumFacing.EAST with Position.P2, EnumFacing.SOUTH with Position.P3, EnumFacing.WEST with Position.P0 -> pos.south().east()
		EnumFacing.NORTH with Position.P2, EnumFacing.EAST with Position.P3, EnumFacing.SOUTH with Position.P0, EnumFacing.WEST with Position.P1 -> pos.south().west()
		EnumFacing.NORTH with Position.P3, EnumFacing.EAST with Position.P0, EnumFacing.SOUTH with Position.P1, EnumFacing.WEST with Position.P2 -> pos.north().west()
		 */
		val (facing, position) = deconstructMeta(getMetaFromState(state))
		return when((facing.binary + position.binary) % 4) {
			0 -> pos.north().east()
			1 -> pos.south().east()
			2 -> pos.south().west()
			3 -> pos.north().west()
			else -> error("Invalid corner!")
		}
	}

	override fun getAABB(state: IBlockState): AxisAlignedBB = FULL_BLOCK_AABB

	override fun place(world: World, pos: BlockPos, facing: EnumFacing) {
		val corners = arrayOf(pos.south().west(), pos.north().west(), pos.north().east(), pos.south().east())
		val direction = facing.binary
		val order = when(direction) {
			Facing.NORTH.binary -> listOf(0, 1, 2, 3)
			Facing.EAST.binary -> listOf(3, 0, 1, 2)
			Facing.SOUTH.binary -> listOf(2, 3, 0, 1)
			Facing.WEST.binary -> listOf(1, 2, 3, 0)
			else -> error("Invalid facing binary: $direction")
		}
		corners.forEachIndexed { idx, corner -> placeBlock(world, corner, facing with Position.entries[order[idx]]) }
	}
}
