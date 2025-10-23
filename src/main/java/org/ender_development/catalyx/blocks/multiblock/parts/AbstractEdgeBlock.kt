package org.ender_development.catalyx.blocks.multiblock.parts

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseBlock
import org.ender_development.catalyx.blocks.multiblock.Facing
import org.ender_development.catalyx.blocks.multiblock.IMultiblockEdge
import org.ender_development.catalyx.blocks.multiblock.IMultiblockTile
import org.ender_development.catalyx.blocks.multiblock.Position
import org.ender_development.catalyx.blocks.multiblock.with
import org.ender_development.catalyx.core.ICatalyxMod

abstract class AbstractEdgeBlock(mod: ICatalyxMod, val name: String) : BaseBlock(mod, name), IMultiblockEdge {
	companion object {
		const val PIXEL_RATIO = 1.0 / 16.0
		val position: PropertyInteger = PropertyInteger.create("position", 0, 3)
	}

	init {
	    disableStats()
	    defaultState = blockState.baseState
			.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
			.withProperty(position, 0)
	}

	override fun deconstructMeta(meta: Int): Pair<Facing, Position> =
		Facing.Companion.fromBinary(meta and 0b1100 shr 2) to Position.entries[meta and 0b0011]

	override fun createBlockState(): BlockStateContainer =
        BlockStateContainer(this, BlockHorizontal.FACING, position)

	/**
	 * Gets the meta from the block state. The meta is constructed as follows:
	 * Meta: 0bFFPP; F - facing, P - position -> which means:
	 * Bit 3-2: Facing (00 - North, 01 - East, 10 - South, 11 - West)
	 * Bit 1-0: Position (00 - Position 0, 01 - Position 1, 10 - Position 2, 11 - Position 3)
	 */
	override fun getMetaFromState(block: IBlockState) =
		block.getValue(BlockHorizontal.FACING) with Position.entries[block.getValue(position)]

	@Deprecated("Implementation is fine.")
	override fun getStateFromMeta(meta: Int): IBlockState {
		val (facing, position) = deconstructMeta(meta)
		return defaultState.withProperty(BlockHorizontal.FACING, facing.facing).withProperty(AbstractEdgeBlock.position, position.binary)
	}

	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		val center = getCenter(pos, state)
		val tileEntity = world.getTileEntity(center)
		if(tileEntity is IMultiblockTile) {
			tileEntity.breakBlock(world, center, world.getBlockState(center), player)
			world.destroyBlock(center, !player.capabilities.isCreativeMode)
		} else
			world.setBlockToAir(center)
	}

	@Suppress("DEPRECATION")
	internal fun placeBlock(world: World, pos: BlockPos, meta: Int) = world.setBlockState(pos, getStateFromMeta(meta), 1 or 2)

	// NO-OP
	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {}

	override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack =
		ItemStack.EMPTY

	@Deprecated("Implementation is fine.")
	override fun getPushReaction(state: IBlockState): EnumPushReaction =
		EnumPushReaction.BLOCK
}
