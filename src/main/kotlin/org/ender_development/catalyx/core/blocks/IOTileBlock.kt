package org.ender_development.catalyx.core.blocks

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.core.blocks.helper.IOProperty
import org.ender_development.catalyx.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx.core.tiles.IOTile
import org.ender_development.catalyx.core.tiles.helper.IPortRenderer

/**
 * NOTE: Please don't worry about the deprecated methods, they are overridden to provide correct rendering behavior.
 * Otherwise, the IO Port rendering would be broken and render way too dark.
 * [isOpaqueCube] returns false to ensure proper transparency handling.
 * [getAmbientOcclusionLightValue] is overridden to still mimic the occlusion level of a non-opaque cube.
 * This implementation also writes the IO port states to the extended block state for rendering.
 * @see net.minecraftforge.client.model.IModel
 * @see net.minecraft.client.renderer.block.model.IBakedModel
 * @see net.minecraftforge.client.model.ICustomModelLoader
 */
open class IOTileBlock(mod: ICatalyxMod, name: String, tileClass: Class<out IOTile>, guiId: Int) : TESRTileBlock(mod, name, tileClass, guiId) {
	companion object {
		val IO_NORTH = IOProperty("io_north")
		val IO_EAST = IOProperty("io_east")
		val IO_SOUTH = IOProperty("io_south")
		val IO_WEST = IOProperty("io_west")
		val IO_UP = IOProperty("io_up")
		val IO_DOWN = IOProperty("io_down")
	}

	override fun createBlockState() =
		ExtendedBlockState(this, arrayOf(BlockHorizontal.FACING), arrayOf(IO_NORTH, IO_EAST, IO_SOUTH, IO_WEST, IO_UP, IO_DOWN))

	override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
		val extendedBlockState = state as IExtendedBlockState
		val tile = world.getTileEntity(pos) as IPortRenderer
		return extendedBlockState
			.withProperty(IO_NORTH, tile.getPortState(EnumFacing.NORTH))
			.withProperty(IO_EAST, tile.getPortState(EnumFacing.EAST))
			.withProperty(IO_SOUTH, tile.getPortState(EnumFacing.SOUTH))
			.withProperty(IO_WEST, tile.getPortState(EnumFacing.WEST))
			.withProperty(IO_UP, tile.getPortState(EnumFacing.UP))
			.withProperty(IO_DOWN, tile.getPortState(EnumFacing.DOWN))
	}

	@Deprecated("Implementation is fine.")
	override fun isOpaqueCube(state: IBlockState) =
		false

	@SideOnly(Side.CLIENT)
	@Deprecated("Implementation is fine.")
	override fun getAmbientOcclusionLightValue(state: IBlockState): Float =
		0.2f
}
