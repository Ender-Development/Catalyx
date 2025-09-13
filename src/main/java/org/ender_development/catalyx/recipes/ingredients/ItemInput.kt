package org.ender_development.catalyx.recipes.ingredients

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.recipes.ingredients.entries.ItemToMetaList
import org.ender_development.catalyx.recipes.ingredients.entries.MetaToTagList
import java.util.*
import java.util.stream.Collectors

class ItemInput : RecipeInput {
	private var inputStacks: List<ItemStack>? = null
	private val itemList: MutableList<ItemToMetaList> = ObjectArrayList()

	constructor(stack: List<ItemStack>?, amount: Int) {
		this.amount = amount
		val lst: NonNullList<ItemStack> = NonNullList.create()
		stack?.forEach {
			if(it.metadata == OreDictionary.WILDCARD_VALUE) {
				it.item.getSubItems(CreativeTabs.SEARCH, lst)
			} else lst.add(it)
		}
		lst.forEach {
			var addedStack = false
			if(it.isEmpty) return@forEach
			itemList.forEach { item ->
				if(item.key == it.item) {
					val metaList: List<MetaToTagList> = item.value
					metaList.forEach { meta ->
						if(meta.intKey == it.metadata) {
							meta.addStackToList(it)
							addedStack = true
							return@forEach
						}
					}
					if(addedStack) return@forEach
					item.addStackToLists(it)
					addedStack = true
					return@forEach
				}
			}
			if(addedStack) return@forEach
			itemList.add(ItemToMetaList(it))
		}
		inputStacks = lst.stream().map {
			val copy = it.copy()
			copy.count = amount
			copy
		}.collect(Collectors.toList())
	}

	constructor(stack: ItemStack) : this(listOf(stack), stack.count)
	constructor(stack: ItemStack, amount: Int) : this(listOf(stack), amount)
	constructor(input: RecipeInput) : this(input.getInputStacks(), input.getAmount())
	constructor(input: RecipeInput, amount: Int) : this(input.getInputStacks(), amount)

	override fun copy(): ItemInput {
		val copy = ItemInput(this.inputStacks, this.amount)
		copy.isConsumable = this.isConsumable
		copy.nbtMatcher = this.nbtMatcher
		copy.nbtCondition = this.nbtCondition
		return copy
	}

	override fun getInputStacks(): List<ItemStack>? = inputStacks

	override fun acceptsStack(input: ItemStack?): Boolean {
		if (input == null || input.isEmpty) return false
		val itemList = this.itemList
		val inputItem = input.item
		itemList.forEach { metaList ->
			if (metaList.item == inputItem) {
				val tagLists = metaList.metaToTagList
				tagLists.forEach { tagList ->
					if (tagList.meta == input.metadata) {
						val inputNBT = input.tagCompound
						if (nbtMatcher != null) {
							return nbtMatcher!!.evaluate(input, nbtCondition)
						} else {
							val tagMaps = tagList.tagToStack
							tagMaps.forEach { tagMapping ->
								if ((inputNBT == null && tagMapping.tag == null) || (inputNBT != null && inputNBT == tagMapping.tag)) return tagMapping.stack.areCapsCompatible(input)
							}
						}
					}
				}
			}
		}
		return false
	}

	override fun computeHash(): Int {
		var hash = 1
		inputStacks?.forEach {
			hash = 31 * hash + it.item.hashCode()
			hash = 31 * hash + it.metadata
			if (it.hasTagCompound() && it.tagCompound != null && nbtMatcher == null) {
				hash = 31 * hash + it.tagCompound.hashCode()
			}
		}
		hash = 31 * hash + amount
		hash = 31 * hash + if (isConsumable) 1 else 0
		hash = 31 * hash + (nbtMatcher?.hashCode() ?: 0)
		hash = 31 * hash + (nbtCondition?.hashCode() ?: 0)
		return hash
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ItemInput) return false
		val that = other as ItemInput?

		if (this.amount != that!!.amount || this.isConsumable != other.isConsumable) return false
		if (Objects.equals(this.nbtMatcher, that.nbtMatcher)) return false
		if (Objects.equals(this.nbtCondition, that.nbtCondition)) return false

		if (this.inputStacks!!.size != that.inputStacks!!.size) return false
		for (i in this.inputStacks!!.indices) {
			if (!ItemStack.areItemStacksEqual(this.inputStacks!![i], that.inputStacks!![i])) return false
		}
		return true
	}

	override fun equalsIgnoreAmount(input: RecipeInput): Boolean {
		if (this === input) return true
		if (input !is ItemInput) return false
		val that = input as ItemInput?

		if (Objects.equals(this.nbtMatcher, that?.nbtMatcher)) return false
		if (Objects.equals(this.nbtCondition, that?.nbtCondition)) return false

		if (this.inputStacks!!.size != that?.inputStacks!!.size) return false
		for (i in this.inputStacks!!.indices) {
			if (!ItemStack.areItemStacksEqual(this.inputStacks!![i], that.inputStacks!![i])) return false
		}
		return true
	}

	override fun toString(): String {
		return when(inputStacks?.size) {
			0 -> "${amount}x[]"
			1 -> "${amount}x${toStringWithoutQuantity(this.inputStacks!![0])}"
			else -> "${amount}x[${this.inputStacks!!.stream().map { toStringWithoutQuantity(it) }.collect(Collectors.joining("|"))}]"
		}
	}

	companion object {
		private fun toStringWithoutQuantity(stack: ItemStack): String = "${stack.item.translationKey}@${stack.itemDamage}"
	}
}
