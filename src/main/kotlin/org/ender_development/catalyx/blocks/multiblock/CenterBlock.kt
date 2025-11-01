package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseRotatableTileBlock
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.utils.extensions.getHorizontalSurroundings

open class CenterBlock<T>(mod: ICatalyxMod, name: String, tileClass: Class<T>, guiId: Int, vararg components: IMultiblockEdge) : BaseRotatableTileBlock(
	mod, name, tileClass, guiId
), IMultiblockCenter where T : TileEntity, T : IMultiblockTile {
	val additionalComponents = components.toList()

	override fun canPlaceBlockAt(world: World, pos: BlockPos) = pos.getHorizontalSurroundings().all { isReplaceable(world, it) } && super.canPlaceBlockAt(world, pos)

	private fun isReplaceable(world: World, pos: BlockPos) = world.getBlockState(pos).block.isReplaceable(world, pos)

	override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) {
		additionalComponents.forEach { it.place(world, pos, state.getValue(BlockHorizontal.FACING)) }
	}

	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		pos.getHorizontalSurroundings().forEach { pos -> (world.getBlockState(pos).block as? IMultiblockEdge)?.let { world.setBlockToAir(pos) } }
		super.onBlockHarvested(world, pos, state, player)
	}
}
