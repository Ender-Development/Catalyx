package org.ender_development.catalyx.core.recipes.ingredients.entries

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ItemToMetaList : Object2ObjectMap.Entry<Item, List<MetaToTagList>> {
	internal var item: Item
	internal var metaToTagList: List<MetaToTagList>

	constructor(stack: ItemStack) {
		this.item = stack.item
		this.metaToTagList = ObjectLists.singleton(MetaToTagList(stack))
	}

	fun addStackToLists(stack: ItemStack) {
		if(metaToTagList is ObjectLists.Singleton) {
			metaToTagList = ObjectArrayList(metaToTagList)
		}
		(metaToTagList as ObjectArrayList).add(MetaToTagList(stack))
	}

	override fun setValue(newValue: List<MetaToTagList>): List<MetaToTagList> {
		metaToTagList = newValue
		return metaToTagList
	}

	override val key: Item
		get() = item

	override val value: List<MetaToTagList>
		get() = metaToTagList
}
