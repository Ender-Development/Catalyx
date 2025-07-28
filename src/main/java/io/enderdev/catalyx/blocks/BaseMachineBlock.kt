package io.enderdev.catalyx.blocks

import io.enderdev.catalyx.CatalyxSettings
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import kotlin.math.roundToInt

/**
 * A Catalyx Block interacting with a TileEntity and a GUI that also interacts with a Comparator
 */
open class BaseMachineBlock(settings: CatalyxSettings, name: String, tileClass: Class<out TileEntity>, guiID: Int) : BaseTileBlock(settings, name, tileClass, guiID) {
	@Deprecated("")
	override fun hasComparatorInputOverride(state: IBlockState) = true

	@Deprecated("")
	override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
		val te = world.getTileEntity(pos)
		if(te == null)
			return 0

		val cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
		if(cap == null)
			return 0

		val slots = cap.slots
		val itemCount = (0..<slots).sumOf {
			cap.getStackInSlot(it).count
		}

		if(itemCount == 0)
			return 0

		return (itemCount.toFloat() / (slots * 64f) * 15).roundToInt()
	}

	override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?) = true
}
