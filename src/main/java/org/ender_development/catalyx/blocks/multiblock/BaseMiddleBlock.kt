package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseTileBlock
import org.ender_development.catalyx.core.ICatalyxMod

open class BaseMiddleBlock(mod: ICatalyxMod, name: String, tileClass: Class<out TileEntity>, guiId: Int, val edge: BaseEdge) : BaseTileBlock(mod, name, tileClass, guiId) {

	private fun getEdges(pos: BlockPos) = listOf(
		pos.north(),
		pos.north().west(),
		pos.west(),
		pos.south().west(),
		pos.south(),
		pos.south().east(),
		pos.east(),
		pos.north().east()
	)

	override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
		val blockPos = listOf(
			pos.east(), pos.west(), pos.north(), pos.south(),
			pos.east().north(), pos.east().south(), pos.west().north(), pos.west().south()
		)
		if(blockPos.all { isReplaceable(world, it) })
			return super.canPlaceBlockAt(world, pos)
		return false
	}

	private fun isReplaceable(world: World, pos: BlockPos) = world.getBlockState(pos).block.isReplaceable(world, pos)

	override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
		getEdges(pos).forEachIndexed { i, p -> world.setBlockState(p, edge.getStateFromMeta(i)) }
	}
}

