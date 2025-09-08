package org.ender_development.catalyx.utils.extensions

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun Item.toStack(quantity: Int = 1, meta: Int = 0) = ItemStack(this, quantity, meta)

fun Item.toIngredient(meta: Int = 0): Ingredient = Ingredient.fromStacks(toStack(meta = meta))

