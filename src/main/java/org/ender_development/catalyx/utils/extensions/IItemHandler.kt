package org.ender_development.catalyx.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

operator fun IItemHandler.get(idx: Int) =
	getStackInSlot(idx)

fun IItemHandler.tryInsertInto(otherHandler: IItemHandler): Boolean {
	for(i in 0..<otherHandler.slots) {
		for(j in 0..<slots) {
			if(!this.getStackInSlot(j).isEmpty) {
				val stackSize = this.getStackInSlot(j).count
				if(otherHandler.insertItem(i, this.extractItem(j, stackSize, true), true).isEmpty) {
					otherHandler.insertItem(i, this.extractItem(j, stackSize, false), false)
					return true
				}
			}
		}
	}
	return false
}

/** This function *will* modify the ItemStack passed in! */
fun IItemHandler.tryInsert(stack: ItemStack): ItemStack {
	for(i in 0..<slots) {
		val inserted = insertItem(i, stack, true)
		if(inserted.isEmpty || inserted.count < stack.count) {
			insertItem(i, stack, false)
			if(inserted.isEmpty)
				return inserted
			else
				stack.shrink(inserted.count)
		}
	}
	return stack
}

fun IItemHandler.toStackList(): List<ItemStack> {
	return (0..<slots).map {
		val stack = this[it]
		if(stack.isEmpty) ItemStack.EMPTY else stack
	}
}
