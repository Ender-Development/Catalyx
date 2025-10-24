package org.ender_development.catalyx.blocks.multiblock.parts

import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.multiblock.Facing
import org.ender_development.catalyx.blocks.multiblock.Facing.Companion.binary
import org.ender_development.catalyx.blocks.multiblock.Position
import org.ender_development.catalyx.blocks.multiblock.with
import org.ender_development.catalyx.core.ICatalyxMod

open class SideBlock(mod: ICatalyxMod, name: String) : AbstractEdgeBlock(mod, name) {
	override fun getCenter(pos: BlockPos, state: IBlockState): BlockPos {
		val (_, position) = deconstructMeta(getMetaFromState(state))
		return when(position) {
			Position.P0 -> pos.north()
			Position.P1 -> pos.east()
			Position.P2 -> pos.south()
			Position.P3 -> pos.west()
		}
	}

	override fun getAABB(state: IBlockState): AxisAlignedBB = FULL_BLOCK_AABB

	override fun place(world: World, pos: BlockPos, facing: EnumFacing) {
		val sides = arrayOf(pos.south(), pos.west(), pos.north(), pos.east())
		val direction = facing.binary
		val order = when(direction) {
			Facing.NORTH.binary -> listOf(0, 1, 2, 3)
			Facing.EAST.binary -> listOf(1, 2, 3, 0)
			Facing.SOUTH.binary -> listOf(2, 3, 0, 1)
			Facing.WEST.binary -> listOf(3, 0, 1, 2)
			else -> error("Invalid facing binary: $direction")
		}
		sides.forEachIndexed { idx, side -> placeBlock(world, side, facing with Position.entries[order[idx]]) }
	}
}
