package org.ender_development.catalyx.utils.extensions

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun Block.toStack(quantity: Int = 1, meta: Int = 0) =
	ItemStack(this, quantity, meta)

fun Block.toIngredient(meta: Int = 0): Ingredient =
	Ingredient.fromStacks(toStack(meta = meta))
