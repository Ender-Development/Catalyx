@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun ItemStack?.orEmpty(): ItemStack =
	this?.takeIf { !isEmpty } ?: ItemStack.EMPTY

@OptIn(ExperimentalContracts::class)
inline fun ItemStack?.isNullOrEmpty(): Boolean {
	contract {
		// yes this is needed
		returns(false) implies (this@isNullOrEmpty is ItemStack)
	}
	return this?.isEmpty != false
}

inline fun ItemStack.orIfNotEmpty(crossinline notEmpty: (ItemStack) -> ItemStack): ItemStack =
	if(isEmpty)
		ItemStack.EMPTY
	else
		notEmpty(this)

fun ItemStack.areStacksEqualIgnoreQuantity(other: ItemStack) =
	item === other.item && metadata == other.metadata && ItemStack.areItemStackTagsEqual(this, other)

fun ItemStack.canMergeWith(target: ItemStack, allowEmpty: Boolean) =
	if(allowEmpty && (isEmpty || target.isEmpty))
		true
	else
		item === target.item && count + target.count <= maxStackSize && itemDamage == target.itemDamage && tagCompound == target.tagCompound

inline fun ItemStack.toIngredient(): Ingredient =
	Ingredient.fromStacks(this)

fun ItemStack.equalsIgnoreMeta(other: ItemStack) =
	if(isEmpty && other.isEmpty)
		true
	else if(!isEmpty && !other.isEmpty)
		item === other.item
	else
		false
