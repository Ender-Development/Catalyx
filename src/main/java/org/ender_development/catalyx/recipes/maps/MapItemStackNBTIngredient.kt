package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx.recipes.ingredients.RecipeInput

class MapItemStackNBTIngredient : MapItemStackIngredient {
	constructor(stack: ItemStack, meta: Int, tag: NBTTagCompound?) : super(stack, meta, tag)

	constructor(s: ItemStack, recipeInput: RecipeInput?) : super(s, s.metadata, null) {
		this.recipeInput = recipeInput
	}

	override fun hash(): Int {
		var hash = stack.getItem().hashCode() * 31
		hash += 31 * meta
		return hash
	}

	override fun equals(other: Any?): Boolean {
		if(this === other) {
			return true
		}
		if(other is MapItemStackNBTIngredient) {
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

	override fun toString(): String {
		return "MapItemStackNBTIngredient{item=${stack.item.getRegistryName()}} {meta=$meta}"
	}

	override val isSpecialIngredient: Boolean = true

	companion object {
		fun from(r: RecipeInput): MutableList<AbstractMapIngredient?> {
			val list = ObjectArrayList<AbstractMapIngredient?>()
			r.getInputStacks()?.forEach {
				list.add(MapItemStackNBTIngredient(it, r))
			}
			return list
		}
	}
}
