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
import org.ender_development.catalyx.utils.extensions.getHorizontalCenterFromMeta
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

	override fun onBlockHarvested(world: World, pos: BlockPos, block: IBlockState, player: EntityPlayer) {
		val center = pos.getHorizontalCenterFromMeta(block.getValue(state))
		val tileEntity = world.getTileEntity(center)
		if(tileEntity is IMultiBlockPart) {
			tileEntity.breakBlock(world, center, world.getBlockState(center), player)
			world.destroyBlock(center, !player.capabilities.isCreativeMode)
		} else {
			world.setBlockToAir(center)
		}
	}

	// NO-OP
	override fun getSubBlocks(itemIn: CreativeTabs, items: NonNullList<ItemStack?>) {}
}
