package org.ender_development.catalyx.core.common.blocks.multiblock

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.extensions.getHorizontalSurroundings
import org.ender_development.catalyx.api.v1.ICatalyxMod
import org.ender_development.catalyx.core.common.blocks.tile.HorizontalTileBlock

/**
 * The most basic implementation of out multiblock system. This is set up to create a 3x1x3 multiblock when placed.
 * It respects horizontal rotations and the surrounding blocks are rotated accordingly. This makes it possible to support
 * non-full-block models by default.
 */
open class CenterBlock<T>(mod: ICatalyxMod, name: String, tileClass: Class<T>, guiId: Int, vararg components: IMultiblockEdge) : HorizontalTileBlock(
	mod, name, tileClass, guiId
), IMultiblockCenter where T : TileEntity, T : IMultiblockTile {
	val additionalComponents = components.toList()

	override fun canPlaceBlockAt(world: World, pos: BlockPos) =
		pos.getHorizontalSurroundings().all { isReplaceable(world, it) } && super.canPlaceBlockAt(world, pos)

	private fun isReplaceable(world: World, pos: BlockPos): Boolean =
		world.getBlockState(pos).block.isReplaceable(world, pos) && world.getEntitiesWithinAABB(EntityLivingBase::class.java, AxisAlignedBB(pos)).isEmpty()

	override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) =
		additionalComponents.forEach { it.place(world, pos, state.getValue(BlockHorizontal.FACING)) }

	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		pos.getHorizontalSurroundings().forEach { pos -> (world.getBlockState(pos).block as? IMultiblockEdge)?.let { world.destroyBlock(pos, false) } }
		super.onBlockHarvested(world, pos, state, player)
	}
}
