package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseBlock
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.utils.extensions.component1
import org.ender_development.catalyx.utils.extensions.component2
import org.ender_development.catalyx.utils.extensions.component3
import org.ender_development.catalyx.utils.extensions.getCentre
import org.ender_development.catalyx.utils.extensions.toStack

open class BaseEdge(mod: ICatalyxMod, name: String) : BaseBlock(mod, name) {
	companion object {
		val state: PropertyInteger = PropertyInteger.create("state", 0, 7)
	}

	override fun createBlockState() =
		BlockStateContainer(this, state)

	override fun getMetaFromState(block: IBlockState): Int =
		block.getValue(state)

	@Deprecated("Implementation is fine.")
	override fun getStateFromMeta(meta: Int): IBlockState =
		defaultState.withProperty(state, meta.coerceIn(0, 7))

	fun breakBlockSafe(world: World, pos: BlockPos, player: EntityPlayer) {
		val te = world.getTileEntity(pos)
		if(te is IMultiBlockPart) {
			te.breakBlock(world, pos, world.getBlockState(pos), player)
			world.destroyBlock(pos, !player.capabilities.isCreativeMode)
		} else
			world.setBlockToAir(pos)
	}

	override fun onBlockHarvested(world: World, pos: BlockPos, block: IBlockState, player: EntityPlayer) {
		// TODO what is this mess
		when(block.getValue(state)) {
			0 -> {
				breakBlockSafe(world, pos.south(), player)
				breakBlockSafe(world, pos.south(2), player)
				breakBlockSafe(world, pos.east(), player)
				breakBlockSafe(world, pos.west(), player)
				breakBlockSafe(world, pos.east().south(), player)
				breakBlockSafe(world, pos.west().south(), player)
				breakBlockSafe(world, pos.east().south(2), player)
				breakBlockSafe(world, pos.west().south(2), player)
			}
			1 -> {
				breakBlockSafe(world, pos.east(), player)
				breakBlockSafe(world, pos.east(2), player)
				breakBlockSafe(world, pos.south(), player)
				breakBlockSafe(world, pos.south(2), player)
				breakBlockSafe(world, pos.east().south(), player)
				breakBlockSafe(world, pos.east(2).south(), player)
				breakBlockSafe(world, pos.east().south(2), player)
				breakBlockSafe(world, pos.east(2).south(2), player)
			}
			2 -> {
				breakBlockSafe(world, pos.east(), player)
				breakBlockSafe(world, pos.east(2), player)
				breakBlockSafe(world, pos.north(), player)
				breakBlockSafe(world, pos.south(), player)
				breakBlockSafe(world, pos.north().east(), player)
				breakBlockSafe(world, pos.south().east(), player)
				breakBlockSafe(world, pos.north().east(2), player)
				breakBlockSafe(world, pos.south().east(2), player)
			}
			3 -> {
				breakBlockSafe(world, pos.east(), player)
				breakBlockSafe(world, pos.east(2), player)
				breakBlockSafe(world, pos.north(), player)
				breakBlockSafe(world, pos.north(2), player)
				breakBlockSafe(world, pos.east().north(), player)
				breakBlockSafe(world, pos.east(2).north(), player)
				breakBlockSafe(world, pos.east().north(2), player)
				breakBlockSafe(world, pos.east(2).north(2), player)
			}
			4 -> {
				breakBlockSafe(world, pos.north(), player)
				breakBlockSafe(world, pos.north(2), player)
				breakBlockSafe(world, pos.east(), player)
				breakBlockSafe(world, pos.west(), player)
				breakBlockSafe(world, pos.east().north(), player)
				breakBlockSafe(world, pos.west().north(), player)
				breakBlockSafe(world, pos.east().north(2), player)
				breakBlockSafe(world, pos.west().north(2), player)
			}
			5 -> {
				breakBlockSafe(world, pos.west(), player)
				breakBlockSafe(world, pos.west(2), player)
				breakBlockSafe(world, pos.north(), player)
				breakBlockSafe(world, pos.north(2), player)
				breakBlockSafe(world, pos.west().north(), player)
				breakBlockSafe(world, pos.west(2).north(), player)
				breakBlockSafe(world, pos.west().north(2), player)
				breakBlockSafe(world, pos.west(2).north(2), player)
			}
			6 -> {
				breakBlockSafe(world, pos.west(), player)
				breakBlockSafe(world, pos.west(2), player)
				breakBlockSafe(world, pos.north(), player)
				breakBlockSafe(world, pos.south(), player)
				breakBlockSafe(world, pos.north().west(), player)
				breakBlockSafe(world, pos.south().west(), player)
				breakBlockSafe(world, pos.north().west(2), player)
				breakBlockSafe(world, pos.south().west(2), player)
			}
			7 -> {
				breakBlockSafe(world, pos.west(), player)
				breakBlockSafe(world, pos.west(2), player)
				breakBlockSafe(world, pos.south(), player)
				breakBlockSafe(world, pos.south(2), player)
				breakBlockSafe(world, pos.west().south(), player)
				breakBlockSafe(world, pos.west(2).south(), player)
				breakBlockSafe(world, pos.west().south(2), player)
				breakBlockSafe(world, pos.west(2).south(2), player)
			}
		}
	}

	// NO-OP
	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {}
}
