package org.ender_development.catalyx.recipes.ingredients.entries

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class TagToStack : Object2ObjectMap.Entry<NBTTagCompound, ItemStack> {
	internal var tag: NBTTagCompound?
	internal var stack: ItemStack

	constructor(tag: NBTTagCompound, stack: ItemStack) {
		this.tag = tag
		this.stack = stack
	}

	constructor(stack: ItemStack) {
		this.tag = stack.tagCompound
		this.stack = stack
	}

	override fun setValue(newValue: ItemStack): ItemStack {
		stack = newValue
		return stack
	}

	override val key: NBTTagCompound?
		get() = tag

	override val value: ItemStack?
		get() = stack
}
