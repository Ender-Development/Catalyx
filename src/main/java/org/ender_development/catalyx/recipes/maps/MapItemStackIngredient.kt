package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx.recipes.ingredients.RecipeInput
import java.util.*

open class MapItemStackIngredient : AbstractMapIngredient {
	val stack: ItemStack
	val meta: Int
	val tag: NBTTagCompound?
	var recipeInput: RecipeInput?

	constructor(stack: ItemStack, meta: Int, tag: NBTTagCompound?) {
		this.stack = stack
		this.meta = meta
		this.tag = tag
		recipeInput = null
	}

	constructor(stack: ItemStack, recipeInput: RecipeInput?) {
		this.stack = stack
		meta = stack.metadata
		tag = stack.tagCompound
		this.recipeInput = recipeInput
	}

	// this doesn't check `tag`?
	override fun equals(other: Any?) =
		this === other || (other is MapItemStackIngredient && stack.item === other.stack.item && meta == other.meta &&
				recipeInput?.let { ours ->
					other.recipeInput?.let {
						ours.equalsIgnoreAmount(it)
					} == true } ?: (other.recipeInput?.acceptsStack(stack) == true)
				)

	override fun hash() =
		Objects.hash(stack.item, meta, tag)

	override fun toString(): String =
		"MapItemStackIngredient{item=${stack.item.registryName}} {meta=$meta} {tag=$tag}"

	companion object {
		fun from(r: RecipeInput): MutableList<AbstractMapIngredient?> {
			val list = ObjectArrayList<AbstractMapIngredient?>()
			r.getInputStacks()?.forEach {
				list.add(MapItemStackIngredient(it, r))
			}
			return list
		}
	}
}
