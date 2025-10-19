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

open class BaseEdge : BaseBlock {
	constructor(mod: ICatalyxMod, name: String) : super(mod, name)

	companion object {
		val state = PropertyInteger.create("state", 0, 7)
	}

	override fun createBlockState() = BlockStateContainer(this, state)

	override fun getMetaFromState(state: IBlockState): Int = state.getValue(BaseEdge.state)

	@Deprecated("Implementation is fine.")
	override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(state, meta)

	fun breakBlockSafe(world: World, pos: BlockPos, player: EntityPlayer) {
		if(world.getTileEntity(pos) is IMultiBlockPart) {
			(world.getTileEntity(pos) as IMultiBlockPart).breakBlock(world, pos, world.getBlockState(pos), player)
			if(world.isRemote && !player.capabilities.isCreativeMode)
				world.spawnEntity(EntityItem(world, pos.x.plus(0.5), pos.y.plus(0.5), pos.z.plus(0.5), ItemStack(world.getBlockState(pos).block)))
		}
		world.setBlockToAir(pos)
	}

	override fun onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer) {
		when(state.getValue(BaseEdge.state)) {
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
				breakBlockSafe(world, pos.east(), player);
				breakBlockSafe(world, pos.east(2), player);
				breakBlockSafe(world, pos.south(), player);
				breakBlockSafe(world, pos.south(2), player);
				breakBlockSafe(world, pos.east().south(), player);
				breakBlockSafe(world, pos.east(2).south(), player);
				breakBlockSafe(world, pos.east().south(2), player);
				breakBlockSafe(world, pos.east(2).south(2), player);
			}
			2 -> {
				breakBlockSafe(world, pos.east(), player);
				breakBlockSafe(world, pos.east(2), player);
				breakBlockSafe(world, pos.north(), player);
				breakBlockSafe(world, pos.south(), player);
				breakBlockSafe(world, pos.north().east(), player);
				breakBlockSafe(world, pos.south().east(), player);
				breakBlockSafe(world, pos.north().east(2), player);
				breakBlockSafe(world, pos.south().east(2), player);
			}
			3 -> {
				breakBlockSafe(world, pos.east(), player);
				breakBlockSafe(world, pos.east(2), player);
				breakBlockSafe(world, pos.north(), player);
				breakBlockSafe(world, pos.north(2), player);
				breakBlockSafe(world, pos.east().north(), player);
				breakBlockSafe(world, pos.east(2).north(), player);
				breakBlockSafe(world, pos.east().north(2), player);
				breakBlockSafe(world, pos.east(2).north(2), player);
			}
			4 -> {
				breakBlockSafe(world, pos.north(), player);
				breakBlockSafe(world, pos.north(2), player);
				breakBlockSafe(world, pos.east(), player);
				breakBlockSafe(world, pos.west(), player);
				breakBlockSafe(world, pos.east().north(), player);
				breakBlockSafe(world, pos.west().north(), player);
				breakBlockSafe(world, pos.east().north(2), player);
				breakBlockSafe(world, pos.west().north(2), player);
			}
			5 -> {
				breakBlockSafe(world, pos.west(), player);
				breakBlockSafe(world, pos.west(2), player);
				breakBlockSafe(world, pos.north(), player);
				breakBlockSafe(world, pos.north(2), player);
				breakBlockSafe(world, pos.west().north(), player);
				breakBlockSafe(world, pos.west(2).north(), player);
				breakBlockSafe(world, pos.west().north(2), player);
				breakBlockSafe(world, pos.west(2).north(2), player);
			}
			6 -> {
				breakBlockSafe(world, pos.west(), player);
				breakBlockSafe(world, pos.west(2), player);
				breakBlockSafe(world, pos.north(), player);
				breakBlockSafe(world, pos.south(), player);
				breakBlockSafe(world, pos.north().west(), player);
				breakBlockSafe(world, pos.south().west(), player);
				breakBlockSafe(world, pos.north().west(2), player);
				breakBlockSafe(world, pos.south().west(2), player);
			}
			7 -> {
				breakBlockSafe(world, pos.west(), player);
				breakBlockSafe(world, pos.west(2), player);
				breakBlockSafe(world, pos.south(), player);
				breakBlockSafe(world, pos.south(2), player);
				breakBlockSafe(world, pos.west().south(), player);
				breakBlockSafe(world, pos.west(2).south(), player);
				breakBlockSafe(world, pos.west().south(2), player);
				breakBlockSafe(world, pos.west(2).south(2), player);
			}
		}
	}

	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {
		// NO-OP
	}
}
