package org.ender_development.catalyx.blocks.multiblock.parts

import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.multiblock.Facing
import org.ender_development.catalyx.blocks.multiblock.Facing.Companion.binary
import org.ender_development.catalyx.blocks.multiblock.Position
import org.ender_development.catalyx.blocks.multiblock.with
import org.ender_development.catalyx_.modules.coremodule.ICatalyxMod

open class SideBlock(mod: ICatalyxMod, name: String) : AbstractEdgeBlock(mod, name) {
	override fun getCenter(pos: BlockPos, state: IBlockState): BlockPos =
		when(normalizeRotation(state)) {
			0 -> pos.north()
			1 -> pos.east()
			2 -> pos.south()
			3 -> pos.west()
			else -> error("Invalid side!")
		}

	override fun place(world: World, pos: BlockPos, facing: EnumFacing) {
		val sides = arrayOf(pos.south(), pos.west(), pos.north(), pos.east())
		val order = when(facing.binary) {
			Facing.NORTH.binary -> listOf(0, 1, 2, 3)
			Facing.EAST.binary -> listOf(3, 0, 1, 2)
			Facing.SOUTH.binary -> listOf(2, 3, 0, 1)
			Facing.WEST.binary -> listOf(1, 2, 3, 0)
			else -> error("Invalid facing binary: ${facing.binary}")
		}
		sides.forEachIndexed { idx, side -> placeBlock(world, side, facing with Position.entries[order[idx]]) }
	}
}
