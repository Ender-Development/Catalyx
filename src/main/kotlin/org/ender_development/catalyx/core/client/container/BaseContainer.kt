package org.ender_development.catalyx.core.client.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.core.tiles.helper.IGuiTile

abstract class BaseContainer(playerInv: IInventory, val tileEntity: IBaseContainerCompat) : Container() {
	init {
		addPlayerSlots(playerInv)
	}

	private var nextSlotId = 0

	fun addSlotArray(x: Int, y: Int, rows: Int, columns: Int, handler: IItemHandler) {
		var index = 0
		repeat(rows) { row ->
			repeat(columns) { column ->
				addSlotToContainer(SlotItemHandler(handler, index++, x + 18 * column, y + 18 * row))
			}
		}
	}

	fun addPlayerSlots(playerInventory: IInventory) {
		repeat(3) { row ->
			repeat(9) { col ->
				val x = 8 + col * 18
				val y = row * 18 + tileEntity.guiHeight - 82
				super.addSlotToContainer(Slot(playerInventory, col + row * 9 + 9, x, y))
			}
		}

		repeat(9) { row ->
			val x = 8 + row * 18
			val y = tileEntity.guiHeight - 24
			super.addSlotToContainer(Slot(playerInventory, row, x, y))
		}
	}

	// required because I wanted to refactor this class a while back to use `init {}` instead of a function, and this is what I get ;p
	override fun addSlotToContainer(slot: Slot): Slot {
		inventorySlots.add(nextSlotId, slot)
		inventoryItemStacks.add(nextSlotId++, ItemStack.EMPTY)

		inventorySlots.forEachIndexed { idx, slot ->
			slot.slotNumber = idx
		}

		return slot
	}

	override fun canInteractWith(player: EntityPlayer) =
		tileEntity.canInteractWith(player)

	override fun transferStackInSlot(player: EntityPlayer, index: Int): ItemStack {
		val slot = inventorySlots[index]
		if(slot == null || !slot.hasStack)
			return ItemStack.EMPTY

		val stack = slot.stack

		// transfer TE Container -> Anywhere else (Player Inventory)
		if(index < tileEntity.SIZE) {
			if(!mergeItemStack(stack, tileEntity.SIZE, inventorySlots.size, true))
				return ItemStack.EMPTY
		// transfer Anywhere else (Player Inventory) -> TE Container
		} else if(!mergeItemStack(stack, 0, tileEntity.SIZE, false))
			return ItemStack.EMPTY

		if(stack.isEmpty)
			slot.putStack(ItemStack.EMPTY)
		else
			slot.onSlotChanged()

		return stack
	}

	interface IBaseContainerCompat : IGuiTile {
		// TODO rename someday
		val SIZE: Int
		fun canInteractWith(player: EntityPlayer): Boolean
	}
}
