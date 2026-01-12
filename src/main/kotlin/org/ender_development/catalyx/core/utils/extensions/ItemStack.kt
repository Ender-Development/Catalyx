package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun ItemStack.areStacksEqualIgnoreQuantity(other: ItemStack) =
	item === other.item && metadata == other.metadata && ItemStack.areItemStackTagsEqual(this, other)

fun ItemStack.canMergeWith(target: ItemStack, allowEmpty: Boolean) =
	if(allowEmpty && (isEmpty || target.isEmpty))
		true
	else
		item === target.item && count + target.count <= maxStackSize && itemDamage == target.itemDamage && tagCompound == target.tagCompound

fun ItemStack.toIngredient(): Ingredient =
	Ingredient.fromStacks(this)

fun ItemStack.equalsIgnoreMeta(other: ItemStack) =
	if(isEmpty && other.isEmpty)
		true
	else if(!isEmpty && !other.isEmpty)
		item === other.item
	else
		false
