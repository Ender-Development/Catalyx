package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

operator fun IItemHandler.get(idx: Int) =
	getStackInSlot(idx)

fun IItemHandler.tryInsertInto(otherHandler: IItemHandler): Boolean {
	for(i in 0..<otherHandler.slots) {
		for(j in 0..<slots) {
			val stack = this[j]
			if(stack.isEmpty)
				continue

			val stackSize = stack.count
			if(otherHandler.insertItem(i, extractItem(j, stackSize, true), true).isEmpty) {
				otherHandler.insertItem(i, extractItem(j, stackSize, false), false)
				return true
			}
		}
	}
	return false
}

/** This function will not modify the ItemStack passed in */
fun IItemHandler.tryInsert(stack: ItemStack): ItemStack {
	if(stack.isEmpty)
		return ItemStack.EMPTY

	var stack = stack.copy()

	for(i in 0..<slots) {
		val remainder = insertItem(i, stack, false)
		if(remainder.isEmpty)
			return ItemStack.EMPTY

		stack = remainder
	}

	return stack
}

fun IItemHandler.toStackList() =
	(0..<slots).map {
		val stack = this[it]
		if(stack.isEmpty) ItemStack.EMPTY else stack
	}
