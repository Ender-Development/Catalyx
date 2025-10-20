package org.ender_development.catalyx.blocks.multiblock

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
import org.ender_development.catalyx.blocks.multiblock.BaseEdge.BinaryFacing.Companion.binary
import org.ender_development.catalyx.core.ICatalyxMod

open class BaseEdge(mod: ICatalyxMod, name: String) : BaseBlock(mod, name) {
	companion object {
		val type: PropertyInteger = PropertyInteger.create("type", 0, 2)
	}

	// Helpers for dealing with meta shenanigans because Ender clearly confused it at some point (at some points it was FFTT, at others it was TTFF ;p)
	// meta: 0bFFTT; F - facing, T - type

	enum class Type(val binary: Int) {
		CORNER(0b00),
		SIDE_1(0b01),
		SIDE_2(0b10)
	}

	enum class BinaryFacing(val binary: Int, val facing: EnumFacing) {
		NORTH(0b00, EnumFacing.NORTH),
		EAST(0b01, EnumFacing.EAST),
		SOUTH(0b10, EnumFacing.SOUTH),
		WEST(0b11, EnumFacing.WEST);

		companion object {
			@Suppress("NOTHING_TO_INLINE")
			inline fun fromBinary(binary: Int) =
				BinaryFacing.entries[binary.coerceIn(0b00, 0b11)]

			internal val EnumFacing.binary: Int
				inline get() = BinaryFacing.entries.first { it.facing === this }.binary
		}
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline infix fun BinaryFacing.with(type: Type) =
		binary shl 2 or type.binary

	@Suppress("NOTHING_TO_INLINE")
	private inline infix fun EnumFacing.with(type: Type) =
		binary shl 2 or type.binary

	@Suppress("NOTHING_TO_INLINE")
	private inline fun unshiftMeta(meta: Int) =
		BinaryFacing.fromBinary(meta and 0b1100 shr 2) to Type.entries[meta and 0b0011]

	// ---

	override fun createBlockState() =
		BlockStateContainer(this, BlockHorizontal.FACING, type)

	override fun getMetaFromState(block: IBlockState) =
		block.getValue(BlockHorizontal.FACING) with Type.entries[block.getValue(type)]

	@Deprecated("Implementation is fine.")
	override fun getStateFromMeta(meta: Int): IBlockState {
		val (facing, type) = unshiftMeta(meta)
		return defaultState.withProperty(BlockHorizontal.FACING, facing.facing).withProperty(Companion.type, type.binary)
	}

	override fun onBlockHarvested(world: World, pos: BlockPos, block: IBlockState, player: EntityPlayer) {
		val center = getCenter(pos, block)
		val tileEntity = world.getTileEntity(center)
		if(tileEntity is IMultiBlockPart) {
			tileEntity.breakBlock(world, center, world.getBlockState(center), player)
			world.destroyBlock(center, !player.capabilities.isCreativeMode)
		} else
			world.setBlockToAir(center)
	}

	/*
	 *      ^N
	 * [WC][N1][NC]
	 * [W2][xx][E2]
	 * [SC][S1][EC]
	 */
	fun placeRing(world: World, pos: BlockPos, facing: EnumFacing) {
		val corners = arrayOf(
			pos.north().east(),
			pos.south().east(),
			pos.south().west(),
			pos.north().west()
		)
		setBlocks(world, pos, BinaryFacing.NORTH with Type.CORNER, BinaryFacing.EAST with Type.CORNER, BinaryFacing.SOUTH with Type.CORNER, BinaryFacing.WEST with Type.CORNER, corners)
		when(facing) {
			EnumFacing.NORTH -> setBlocks(world, pos, BinaryFacing.NORTH with Type.SIDE_1, BinaryFacing.EAST with Type.SIDE_2, BinaryFacing.SOUTH with Type.SIDE_1, BinaryFacing.WEST with Type.SIDE_2)
			EnumFacing.EAST  -> setBlocks(world, pos, BinaryFacing.NORTH with Type.SIDE_2, BinaryFacing.EAST with Type.SIDE_1, BinaryFacing.SOUTH with Type.SIDE_2, BinaryFacing.WEST with Type.SIDE_1)
			EnumFacing.SOUTH -> setBlocks(world, pos, BinaryFacing.SOUTH with Type.SIDE_1, BinaryFacing.WEST with Type.SIDE_2, BinaryFacing.NORTH with Type.SIDE_1, BinaryFacing.EAST with Type.SIDE_2)
			EnumFacing.WEST  -> setBlocks(world, pos, BinaryFacing.SOUTH with Type.SIDE_2, BinaryFacing.WEST with Type.SIDE_1, BinaryFacing.NORTH with Type.SIDE_2, BinaryFacing.EAST with Type.SIDE_1)
			else -> error("Impossible facing for horizontal multiblock: $facing")
		}
	}

	fun getCenter(pos: BlockPos, state: IBlockState): BlockPos {
		val meta = getMetaFromState(state)
		val (facing, type) = unshiftMeta(meta)
		// TODO for Ender - verify if this is correct
		// TODO for roz - west and south facing middle blocks don't find the center correctly
		return when(type) {
			Type.CORNER -> when(facing) {
				BinaryFacing.NORTH -> pos.south().west()
				BinaryFacing.EAST  -> pos.north().west()
				BinaryFacing.SOUTH -> pos.north().east()
				BinaryFacing.WEST  -> pos.south().east()
			}
			//Type.SIDE_1, Type.SIDE_2 -> pos.offset(facing.facing)
			else -> when(facing) {
				BinaryFacing.NORTH -> pos.north()
				BinaryFacing.EAST  -> pos.east()
				BinaryFacing.SOUTH -> pos.south()
				BinaryFacing.WEST  -> pos.west()
			}
		}
	}

	private fun setBlocks(world: World, origin: BlockPos, top: Int, right: Int, bottom: Int, left: Int, listPos: Array<BlockPos> = arrayOf(origin.north(), origin.east(), origin.south(), origin.west())) {
		val metas = arrayOf(top, right, bottom, left)
		@Suppress("DEPRECATION")
		listPos.forEachIndexed { idx, pos ->
			world.setBlockState(pos, getStateFromMeta(metas[idx]), 1 or 2)
		}
	}

	// NO-OP
	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {}

	override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack = ItemStack.EMPTY

	@Deprecated("Implementation is fine.")
	override fun getPushReaction(state: IBlockState): EnumPushReaction = EnumPushReaction.BLOCK
}
