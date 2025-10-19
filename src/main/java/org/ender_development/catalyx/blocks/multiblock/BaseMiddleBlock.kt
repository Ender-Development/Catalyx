package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseTileBlock
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.utils.extensions.getHorizontalSurroundings

open class BaseMiddleBlock<T>(mod: ICatalyxMod, name: String, tileClass: Class<T>, guiId: Int, val edge: BaseEdge) : BaseTileBlock(mod, name, tileClass, guiId) where T : TileEntity, T : IMultiBlockPart {
	override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
		val blockPos = arrayOf(
			pos.east(), pos.west(), pos.north(), pos.south(),
			pos.east().north(), pos.east().south(), pos.west().north(), pos.west().south()
		)
		return blockPos.all { isReplaceable(world, it) } && super.canPlaceBlockAt(world, pos)
	}

	private fun isReplaceable(world: World, pos: BlockPos) =
		world.getBlockState(pos).block.isReplaceable(world, pos)

	override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
		pos.getHorizontalSurroundings().forEachIndexed { idx, pos ->
			@Suppress("DEPRECATION")
			world.setBlockState(pos, edge.getStateFromMeta(idx))
		}
	}
}

