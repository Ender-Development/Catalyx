package org.ender_development.catalyx.core.blocks.multiblock

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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
	/**
	 * Called when any part of the multiblock structure is activated (right-clicked) by a player.
	 *
	 * @param world The world where the block is located.
	 * @param pos The position of the block.
	 * @param state The block state of the block.
	 * @param player The player who activated the block.
	 * @param hand The hand used to activate the block.
	 * @param side The side of the block that was clicked.
	 * @param hitX The X coordinate of the hit vector.
	 * @param hitY The Y coordinate of the hit vector.
	 * @param hitZ The Z coordinate of the hit vector.
	 * @return True if the activation was successful, false otherwise.
	 */
	fun activate(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Double, hitY: Double, hitZ: Double): Boolean

	/**
	 * Called when any part of the multiblock structure is broken by a player.
	 * Usually used to handle cleanup and breaking of the entire multiblock structure.
	 *
	 * @param world The world where the block is located.
	 * @param pos The position of the block.
	 * @param state The block state of the block.
	 * @param player The player who broke the block.
	 */
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
	 * Deconstructs the metadata to get the facing and position type.
	 *
	 * @param meta The metadata of the block.
	 * @return A pair containing the [Facing] and [Position] type.
	 */
	fun deconstructMeta(meta: Int): Pair<Facing, Position>

	/**
	 * Normalizes the rotation of the edge block based on its state.
	 *
	 * @param state The block state of the edge block.
	 * @return An integer representing the normalized [Position] index (0-3).
	 */
	fun normalizeRotation(state: IBlockState): Int

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
