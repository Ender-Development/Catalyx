package org.ender_development.catalyx.core.blocks

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.core.ICatalyxMod

open class BaseRotatableTileBlock : BaseTileBlock {
	constructor(mod: ICatalyxMod, name: String, tileClass: Class<out TileEntity>, guiId: Int) : super(mod, name, tileClass, guiId)
	/**
	 * Only use this constructor if you used a [org.ender_development.catalyx.client.gui.CatalyxGuiHandler] for the guiId
	 */
	constructor(mod: ICatalyxMod, name: String, guiId: Int) : super(mod, name, guiId)

	init {
		defaultState = blockState.baseState.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
	}

	override fun createBlockState() =
		BlockStateContainer(this, BlockHorizontal.FACING)

	override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState =
		super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(BlockHorizontal.FACING, placer.horizontalFacing.opposite)

	@Deprecated("")
	override fun getStateFromMeta(meta: Int): IBlockState =
		defaultState.withProperty(BlockHorizontal.FACING, EnumFacing.HORIZONTALS[meta])

	override fun getMetaFromState(state: IBlockState): Int =
		EnumFacing.HORIZONTALS.indexOf(state.getValue(BlockHorizontal.FACING))
}
