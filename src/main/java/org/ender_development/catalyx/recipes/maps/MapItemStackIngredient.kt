package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx.recipes.ingredients.RecipeInput

open class MapItemStackIngredient : AbstractMapIngredient {
	var stack: ItemStack
	var meta: Int
	var tag: NBTTagCompound?
	var recipeInput: RecipeInput? = null

	constructor(stack: ItemStack, meta: Int, tag: NBTTagCompound?) {
		this.stack = stack
		this.meta = meta
		this.tag = tag
	}

	constructor(stack: ItemStack, recipeInput: RecipeInput?) {
		this.stack = stack
		this.meta = stack.metadata
		this.tag = stack.tagCompound
		this.recipeInput = recipeInput
	}

	override fun equals(other: Any?): Boolean {
		if(super.equals(other)) {
			val other = other as MapItemStackIngredient
			if(this.stack.getItem() !== other.stack.getItem()) {
				return false
			}
			if(this.meta != other.meta) {
				return false
			}
			if(this.recipeInput != null) {
				other.recipeInput?.let { return recipeInput!!.equalsIgnoreAmount(it) }
			} else if(other.recipeInput != null) {
				return other.recipeInput!!.acceptsStack(this.stack)
			}
		}
		return false
	}

	override fun hash(): Int {
		var hash = stack.getItem().hashCode() * 31
		hash += 31 * this.meta
		hash += 31 * (if(this.tag != null) this.tag.hashCode() else 0)
		return hash
	}

	override fun toString(): String =
		"MapItemStackIngredient{item=${stack.item.getRegistryName()}} {meta=$meta} {tag=$tag}"

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
