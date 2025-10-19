package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseBlock
import org.ender_development.catalyx.core.ICatalyxMod

open class BaseEdge(mod: ICatalyxMod, name: String) : BaseBlock(mod, name) {
	companion object {
		enum class Type(val binary: Int) { CORNER(0b00), SIDE_1(0b01), SIDE_2(0b10) }

		val type: PropertyInteger = PropertyInteger.create("type", 0, 2)
	}

	override fun createBlockState() =
		BlockStateContainer(this, BlockHorizontal.FACING, type)

	override fun getMetaFromState(block: IBlockState): Int {
		val facing = when(block.getValue(BlockHorizontal.FACING)) {
			EnumFacing.NORTH -> 0b00
			EnumFacing.EAST -> 0b01
			EnumFacing.SOUTH -> 0b10
			EnumFacing.WEST -> 0b11
			else -> 0
		}
		val type = Type.entries[block.getValue(type)].binary
		return (facing shl 2) or type
	}

	@Deprecated("Implementation is fine.")
	override fun getStateFromMeta(meta: Int): IBlockState =
		defaultState.withProperty(type, ((meta and 0b1100).shr(2)).coerceAtMost(2)).withProperty(
			BlockHorizontal.FACING, when(meta and 0b0011) {
				0b00 -> EnumFacing.NORTH
				0b01 -> EnumFacing.EAST
				0b10 -> EnumFacing.SOUTH
				0b11 -> EnumFacing.WEST
				else -> EnumFacing.NORTH
			}
		)

	override fun onBlockHarvested(world: World, pos: BlockPos, block: IBlockState, player: EntityPlayer) {
		val center = getCenter(pos, block)
		val tileEntity = world.getTileEntity(center)
		if(tileEntity is IMultiBlockPart) {
			tileEntity.breakBlock(world, center, world.getBlockState(center), player)
			world.destroyBlock(center, !player.capabilities.isCreativeMode)
		} else {
			world.setBlockToAir(center)
		}
	}

	fun placeRing(world: World, pos: BlockPos, facing: EnumFacing) {
		val listCorners = listOf(
			pos.north().east(),
			pos.south().east(),
			pos.south().west(),
			pos.north().west()
		)
		setBlocks(world, pos, 0b0000, 0b0001, 0b0010, 0b0011, listCorners)
		when(facing) {
			EnumFacing.NORTH -> {
				setBlocks(world, pos, 4, 9, 6, 11)
			}
			EnumFacing.EAST -> {
				setBlocks(world, pos, 8, 5, 10, 7)
			}
			EnumFacing.SOUTH -> {
				setBlocks(world, pos, 6, 11, 4, 9)
			}
			EnumFacing.WEST -> {
				setBlocks(world, pos, 10, 7, 8, 5)
			}
			else -> error("Impossible facing for horizontal multiblock: $facing")
		}
	}

	fun getCenter(pos: BlockPos, blockstate: IBlockState): BlockPos {
		return when(this.getMetaFromState(blockstate)) {
			0 -> pos.south().west()
			1 -> pos.north().west()
			2 -> pos.north().east()
			3 -> pos.south().east()
			4, 8 -> pos.south()
			5, 9 -> pos.west()
			6, 10 -> pos.north()
			7, 11 -> pos.east()
			// TODO how can this be something greater than 12 so reliable?
			else -> error("Impossible meta for horizontal multiblock: ${getMetaFromState(blockstate)}")
		}
	}

	@Suppress("DEPRECATION")
	private fun setBlocks(
		world: World,
		origin: BlockPos,
		top: Int,
		right: Int,
		bottom: Int,
		left: Int,
		listPos: List<BlockPos> = listOf(origin.north(), origin.east(), origin.south(), origin.west())
	) {
		val listInt = listOf(top, right, bottom, left)
		listPos.associate { it to listInt[listPos.indexOf(it)] }.forEach { (p, m) -> world.setBlockState(p, getStateFromMeta(m), 3) }
	}

	// NO-OP
	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {}
}
