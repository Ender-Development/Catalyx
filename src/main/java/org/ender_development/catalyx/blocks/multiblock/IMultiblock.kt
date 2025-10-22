package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Marker interface for multiblock components.
 */
interface IMultiblock

/**
 * Interface for multiblock tile entities.
 */
interface IMultiblockTile : IMultiblock {
	fun activate(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Double, hitY: Double, hitZ: Double): Boolean

	fun breakBlock(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer)
}

/**
 * Interface for the center block of a multiblock structure.
 */
interface IMultiblockCenter : IMultiblock {
	/**
	 * Checks if the multiblock can be placed at the given position.
	 *
	 * @param world The world where the block is being placed.
	 * @param pos The position where the block is being placed.
	 * @return True if the block can be placed, false otherwise.
	 */
	fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean

	/**
	 * Called when the multiblock center block is added to the world.
	 * Used to place the surrounding edges.
	 *
	 * @param world The world where the block is added.
	 * @param pos The position where the block is added.
	 * @param state The block state of the added block.
	 */
	fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState)

	/**
	 * Called when the multiblock center block is harvested (broken) by a player.
	 * Used to remove the surrounding edges.
	 *
	 * @param world The world where the block is harvested.
	 * @param pos The position where the block is harvested.
	 * @param state The block state of the harvested block.
	 * @param player The player who harvested the block.
	 */
	fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer)
}

/**
 * Interface for the edge blocks of a multiblock structure.
 */
interface IMultiblockEdge : IMultiblock {
	/**
	 * Gets the center position of the multiblock structure based on the edge block's state.
	 *
	 * @param pos The position of the edge block.
	 * @param state The block state of the edge block.
	 * @return The position of the center block.
	 */
	fun getCenter(pos: BlockPos, state: IBlockState): BlockPos

	/**
	 * Gets the Axis-Aligned Bounding Box (AABB) for the edge block based on its state.
	 *
	 * @param state The block state of the edge block.
	 * @return The AABB of the edge block.
	 */
	fun getAABB(state: IBlockState): AxisAlignedBB

	/**
	 * Deconstructs the metadata to get the facing and position type.
	 *
	 * @param meta The metadata of the block.
	 * @return A pair containing the [Facing] and [Position] type.
	 */
	fun deconstructMeta(meta: Int): Pair<Facing, Position>

	/**
	 * Places the edge block in the world at the specified position.
	 *
	 * @param world The world where the block is being placed.
	 * @param pos The position where the block is being placed.
	 * @param facing The facing direction of the center block.
	 */
	fun place(world: World, pos: BlockPos, facing: EnumFacing)

	/**
	 * Called when the multiblock edge block is harvested (broken) by a player.
	 * Used to remove the center block if necessary.
	 *
	 * @param world The world where the block is harvested.
	 * @param pos The position where the block is harvested.
	 * @param state The block state of the harvested block.
	 * @param player The player who harvested the block.
	 */
	fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer)
}
