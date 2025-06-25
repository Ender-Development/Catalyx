package io.enderdev.catalyx.client.container

import io.enderdev.catalyx.tiles.helper.IGuiTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

abstract class BaseContainer(playerInv: IInventory, val tileEntity: IBaseContainerCompat) : Container() {
	init {
		addPlayerSlots(playerInv)
	}

	fun addSlotArray(x: Int, y: Int, rows: Int, columns: Int, handler: IItemHandler) {
		var index = 0
		for(row in 0..<rows)
			for(column in 0..<columns)
				addSlotToContainer(SlotItemHandler(handler, index++, x + 18 * column, y + 18 * row))
	}

	fun addPlayerSlots(playerInventory: IInventory) {
		for(row in 0..2) {
			for(col in 0..8) {
				val x = 8 + col * 18
				val y = row * 18 + tileEntity.guiHeight - 82
				this.addSlotToContainer(Slot(playerInventory, col + row * 9 + 9, x, y))
			}
		}

		for(row in 0..8) {
			val x = 8 + row * 18
			val y = tileEntity.guiHeight - 24
			this.addSlotToContainer(Slot(playerInventory, row, x, y))
		}
	}

	override fun canInteractWith(player: EntityPlayer): Boolean = tileEntity.canInteractWith(player)

	override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack {
		var itemstack = ItemStack.EMPTY
		val slot = this.inventorySlots[index]
		if(slot != null && slot.hasStack) {
			val itemstack1 = slot.stack
			itemstack = itemstack1.copy()

			if(index < tileEntity.SIZE) {
				if(!this.mergeItemStack(itemstack1, tileEntity.SIZE, this.inventorySlots.size, true)) {
					return ItemStack.EMPTY
				}
			} else if(!this.mergeItemStack(itemstack1, 0, tileEntity.SIZE, false)) return ItemStack.EMPTY

			if(itemstack1.count <= 0) slot.putStack(ItemStack.EMPTY)
			else slot.onSlotChanged()
		}
		return itemstack
	}

	interface IBaseContainerCompat : IGuiTile {
		val SIZE: Int
		fun canInteractWith(player: EntityPlayer): Boolean
	}
}
