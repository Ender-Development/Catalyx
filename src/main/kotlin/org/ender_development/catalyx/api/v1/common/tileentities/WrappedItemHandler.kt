package org.ender_development.catalyx.api.v1.common.tileentities

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

open class WrappedItemHandler(private val internalHandler: IItemHandlerModifiable) : IItemHandlerModifiable by internalHandler {
	override fun isItemValid(slot: Int, stack: ItemStack) =
		internalHandler.isItemValid(slot, stack)
}
