package org.ender_development.catalyx.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.tiles.BaseTile

/**
 * A Catalyx Block interacting with a TileEntity and a GUI that also interacts with a Comparator
 */
open class BaseMachineBlock(settings: CatalyxSettings, name: String, tileClass: Class<out TileEntity>, guiID: Int) : BaseTileBlock(settings, name, tileClass, guiID) {
	@Deprecated("")
	override fun hasComparatorInputOverride(state: IBlockState) =
		true

	@Deprecated("")
	override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
		val te = world.getTileEntity(pos) ?: return 0
		val cap = te.getCapability(BaseTile.ITEM_CAP, null) ?: return 0

		var itemCount = 0
		var itemStackSizes = 0

		(0..<cap.slots).forEach {
			val stack = cap.getStackInSlot(it)
			if(!stack.isEmpty) {
				itemCount += stack.count
				itemStackSizes += stack.maxStackSize
			}
		}

		if(itemCount == 0 || itemStackSizes == 0)
			return 0

		return (itemCount * 15) / itemStackSizes
	}

	override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?) =
		true
}
