package org.ender_development.catalyx_.core.recipes.ingredients.entries

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.item.ItemStack

class MetaToTagList : Int2ObjectMap.Entry<List<TagToStack>> {
	internal var meta: Int
	internal var tagToStack: List<TagToStack>

	constructor(stack: ItemStack) {
		this.meta = stack.metadata
		this.tagToStack = ObjectLists.singleton(TagToStack(stack))
	}

	fun addStackToList(stack: ItemStack) {
		if(tagToStack is ObjectLists.Singleton) {
			tagToStack = ObjectArrayList(tagToStack)
		}
		(tagToStack as ObjectArrayList).add(TagToStack(stack))
	}

	override fun getIntKey() =
		meta

	override fun setValue(newValue: List<TagToStack>): List<TagToStack> {
		tagToStack = newValue
		return tagToStack
	}

	@Suppress("OVERRIDE_DEPRECATION")
	override val key: Int
		get() = meta

	override val value: List<TagToStack>
		get() = tagToStack
}
