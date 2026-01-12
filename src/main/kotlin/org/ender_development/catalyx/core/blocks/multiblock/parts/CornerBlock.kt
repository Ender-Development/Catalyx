package org.ender_development.catalyx.core.blocks.multiblock.parts

import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.core.blocks.multiblock.Facing
import org.ender_development.catalyx.core.blocks.multiblock.Facing.Companion.binary
import org.ender_development.catalyx.core.blocks.multiblock.Position
import org.ender_development.catalyx.core.blocks.multiblock.with
import org.ender_development.catalyx.modules.coremodule.ICatalyxMod

open class CornerBlock(mod: ICatalyxMod, name: String) : AbstractEdgeBlock(mod, name) {
	override fun getCenter(pos: BlockPos, state: IBlockState): BlockPos =
		when(normalizeRotation(state)) {
			0 -> pos.north().east()
			1 -> pos.south().east()
			2 -> pos.south().west()
			3 -> pos.north().west()
			else -> error("Invalid corner!")
		}

	override fun place(world: World, pos: BlockPos, facing: EnumFacing) {
		val corners = arrayOf(pos.south().west(), pos.north().west(), pos.north().east(), pos.south().east())
		val order = when(facing.binary) {
			Facing.NORTH.binary -> listOf(0, 1, 2, 3)
			Facing.EAST.binary -> listOf(3, 0, 1, 2)
			Facing.SOUTH.binary -> listOf(2, 3, 0, 1)
			Facing.WEST.binary -> listOf(1, 2, 3, 0)
			else -> error("Invalid facing binary: ${facing.binary}")
		}
		corners.forEachIndexed { idx, corner -> placeBlock(world, corner, facing with Position.entries[order[idx]]) }
	}
}
