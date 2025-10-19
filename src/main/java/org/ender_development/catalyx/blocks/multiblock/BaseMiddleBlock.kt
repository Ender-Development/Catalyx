package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseRotatableTileBlock
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.utils.extensions.getHorizontalSurroundings

open class BaseMiddleBlock<T>(mod: ICatalyxMod, name: String, tileClass: Class<T>, guiId: Int, val edge: BaseEdge) : BaseRotatableTileBlock(mod, name, tileClass, guiId) where T : TileEntity, T : IMultiBlockPart {
	override fun canPlaceBlockAt(world: World, pos: BlockPos) =
		pos.getHorizontalSurroundings().all { isReplaceable(world, it) } && super.canPlaceBlockAt(world, pos)

	private fun isReplaceable(world: World, pos: BlockPos) =
		world.getBlockState(pos).block.isReplaceable(world, pos)

	override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
		edge.placeRing(world, pos, EnumFacing.byHorizontalIndex(getMetaFromState(world.getBlockState(pos))))
	}

	override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		pos.getHorizontalSurroundings().forEach{ pos -> (worldIn.getBlockState(pos).block as? BaseEdge)?.let { worldIn.setBlockToAir(pos) } }
		super.onBlockHarvested(worldIn, pos, state, player)
	}
}
