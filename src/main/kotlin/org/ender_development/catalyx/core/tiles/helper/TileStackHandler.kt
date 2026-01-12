package org.ender_development.catalyx.core.tiles.helper

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler
import org.ender_development.catalyx.core.tiles.BaseTile
import org.ender_development.catalyx.core.tiles.BaseTile.Companion.ITEM_CAP
import org.ender_development.catalyx.core.utils.extensions.get
import org.ender_development.catalyx.core.utils.extensions.tryInsertInto

open class TileStackHandler(size: Int, val tile: BaseTile) : ItemStackHandler() {
	init {
		setSize(size)
	}

	override fun onContentsChanged(slot: Int) {
		super.onContentsChanged(slot)
		tile.markDirty()
	}

	fun clear() = (0..<slots).forEach { setStackInSlot(it, ItemStack.EMPTY) }

	fun incrementSlot(slot: Int, amountToAdd: Int) {
		val temp = this[slot]
		temp.count = (temp.count + amountToAdd).coerceAtMost(temp.maxStackSize)
		setStackInSlot(slot, temp)
	}

	fun setOrIncrement(slot: Int, stackToSet: ItemStack) {
		if(!stackToSet.isEmpty) {
			if(this[slot].isEmpty) setStackInSlot(slot, stackToSet)
			else incrementSlot(slot, stackToSet.count)
		}
	}

	/** Try to forcibly add/insert an item into this TSH.
	 *  This method *will* modify the ItemStack! */
	fun insert(stack: ItemStack): ItemStack {
		if(stack.isEmpty)
			return stack

		for(slot in 0..<slots) {
			val slotItem = this[slot]
			if(slotItem.isEmpty) {
				setStackInSlot(slot, stack)
				return ItemStack.EMPTY
			}
			if(slotItem.item === stack.item) {
				val increaseBy = stack.count.coerceAtMost(slotItem.maxStackSize - slotItem.count)
				if(increaseBy > 0) {
					incrementSlot(slot, increaseBy)
					stack.shrink(increaseBy)
					if(stack.count <= 0 || stack.isEmpty)
						return ItemStack.EMPTY
				}
			}
		}
		return stack
	}

	fun decrementSlot(slot: Int, amount: Int) {
		val temp = this[slot]
		if(temp.isEmpty) return
		if(temp.count - amount < 0) return

		temp.shrink(amount)
		if(temp.count <= 0) this.setStackInSlot(slot, ItemStack.EMPTY)
		else this.setStackInSlot(slot, temp)
	}

	fun eject(direction: EnumFacing): Boolean {
		val originHandler = tile.getCapability(ITEM_CAP, direction)
		val targetHandler = tile.world.getTileEntity(tile.pos.offset(direction))
			?.getCapability(ITEM_CAP, direction.opposite)

		return if(originHandler != null && targetHandler != null) originHandler.tryInsertInto(targetHandler)
		else false
	}
}
