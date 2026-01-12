package org.ender_development.catalyx.core.recipes.maps

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import org.ender_development.catalyx.core.recipes.ingredients.RecipeInput
import java.util.*

class MapItemStackNBTIngredient : MapItemStackIngredient {
	constructor(stack: ItemStack, meta: Int, tag: NBTTagCompound?) : super(stack, meta, tag)

	constructor(s: ItemStack, recipeInput: RecipeInput?) : super(s, s.metadata, null) {
		this.recipeInput = recipeInput
	}

	override fun hash() =
		Objects.hash(stack.item, meta)

	// this doesn't check `tag`?
	override fun equals(other: Any?) =
		this === other || (other is MapItemStackNBTIngredient && stack.item === other.stack.item && meta == other.meta &&
				recipeInput?.let { ours ->
					other.recipeInput?.let {
						ours.equalsIgnoreAmount(it)
					} == true } ?: (other.recipeInput?.acceptsStack(stack) == true)
				)

	override fun toString() =
		"MapItemStackNBTIngredient{item=${stack.item.registryName}} {meta=$meta}"

	override val isSpecialIngredient = true

	companion object {
		fun from(r: RecipeInput): MutableList<AbstractMapIngredient> {
			val list = ObjectArrayList<AbstractMapIngredient>()
			r.getInputStacks()?.forEach {
				list.add(MapItemStackNBTIngredient(it, r))
			}
			return list
		}
	}
}
