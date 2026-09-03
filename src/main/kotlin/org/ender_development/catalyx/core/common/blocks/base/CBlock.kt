package org.ender_development.catalyx.core.common.blocks.base

import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import org.ender_development.catalyx.api.v1.ICatalyxMod

/**
 * The base class for all other block implementation used in catalyx and its mods.
 * This extends our [CatBlock] with additional checks, like on demand hitbox detection
 * and other calculations based on the [AxisAlignedBB]
 */
open class CBlock(mod: ICatalyxMod, name: String, material: Material = Material.ROCK, hardness: Float = 3f) : CatBlock(mod, name, material) {
	init {
	    blockHardness = hardness
	}

	companion object {
		/**
		 * Ratio for per pixel calculation for [net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer]
		 * It results out if dividing a whole block length through its 16 pixels of the texture per length
		 */
		const val PIXEL_RATIO = 1.0 / 16.0
	}

	/**
	 * Gets the Axis-Aligned Bounding Box (AABB) for the block based on its state.
	 * This is later used to calculate connections to blocks like fences, walls, etc.
	 *
	 * @param state The block state of the block.
	 * @return The AABB of the block.
	 */
	open fun getAABB(state: IBlockState): AxisAlignedBB =
		FULL_BLOCK_AABB

	/**
	 * We override this method to let it utilize our [getAABB] method.
	 */
	@Deprecated("Implementation is fine")
	override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB =
		getAABB(state)

	/**
	 * We override this method to let it utilize our [getAABB] method.
	 *
	 * @param state The block state of the block.
	 * @return If the block has a [net.minecraft.block.Block.FULL_BLOCK_AABB]
	 */
	@Deprecated("Implementation is fine")
	override fun isFullCube(state: IBlockState): Boolean =
		getAABB(state) == FULL_BLOCK_AABB

	/**
	 * We override this method to let it utilize our [getAABB] method.
	 *
	 * @param state The block state of the block.
	 * @return If the block has a [net.minecraft.block.Block.FULL_BLOCK_AABB] and is opaque
	 */
	@Suppress("Deprecation")
	@Deprecated("Implementation is fine")
	override fun isFullBlock(state: IBlockState): Boolean =
		isFullCube(state) && defaultState.isOpaqueCube

	/**
	 * We override this method to let it utilize our [getAABB] method.
	 * Furthermore, we dynamically detect the shape based on the AABB.
	 * So that we don't have to manually implemented connection to walls, fences, panes.
	 *
	 * @param worldIn The world the block is placed in
	 * @param state The block state of the block
	 * @param pos The position the block is placed at
	 * @param face The side we want to check for
	 * @return Either [BlockFaceShape.SOLID] or [BlockFaceShape.UNDEFINED]
	 *
	 */
	@Deprecated("Implementation is fine.")
	override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
		val aabb = getAABB(state)
		return when (face) {
			EnumFacing.UP -> if(aabb.maxY >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.DOWN -> if(aabb.minY <= .0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.NORTH -> if(aabb.minZ <= .0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.SOUTH -> if(aabb.maxZ >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.WEST -> if(aabb.minX <= .0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.EAST -> if(aabb.maxX >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
		}
	}
}
