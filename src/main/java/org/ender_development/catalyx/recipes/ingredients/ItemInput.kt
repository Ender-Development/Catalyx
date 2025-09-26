package org.ender_development.catalyx.recipes.ingredients

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.recipes.ingredients.entries.ItemToMetaList
import java.util.*

class ItemInput : RecipeInput {
	private var inputStacks: List<ItemStack>? = null
	private val itemList: MutableList<ItemToMetaList> = ObjectArrayList()

	constructor(stack: List<ItemStack>?, amount: Int) {
		this.amount = amount
		val lst: NonNullList<ItemStack> = NonNullList.create()
		stack?.forEach {
			if(it.metadata == OreDictionary.WILDCARD_VALUE)
				it.item.getSubItems(CreativeTabs.SEARCH, lst)
			else
				lst.add(it)
		}

		lst.forEach main@{
			if(it.isEmpty)
				return@main

			itemList.forEach outer@{ item ->
				if(item.item === it.item) {
					val metaList = item.metaToTagList
					metaList.forEach { meta ->
						if(meta.meta == it.metadata) {
							meta.addStackToList(it)
							return@main
						}
					}

					item.addStackToLists(it)
					return@main
				}
			}

			itemList.add(ItemToMetaList(it))
		}

		inputStacks = lst.map {
			it.copy().let { copy ->
				copy.count = amount
			}
		}
	}

	constructor(stack: ItemStack) : this(listOf(stack), stack.count)
	constructor(stack: ItemStack, amount: Int) : this(listOf(stack), amount)
	constructor(input: RecipeInput) : this(input.getInputStacks(), input.amount)
	constructor(input: RecipeInput, amount: Int) : this(input.getInputStacks(), amount)

	override fun copy(): ItemInput {
		val copy = ItemInput(inputStacks, amount)
		copy.isConsumable = isConsumable
		copy.nbtMatcher = nbtMatcher
		copy.nbtCondition = nbtCondition
		return copy
	}

	override fun getInputStacks(): List<ItemStack>? =
		inputStacks

	override fun acceptsStack(stack: ItemStack?): Boolean {
		if(stack == null || stack.isEmpty)
			return false

		itemList.forEach { metaList ->
			if(metaList.item === stack.item) {
				val tagLists = metaList.metaToTagList
				tagLists.forEach { tagList ->
					if(tagList.meta == stack.metadata) {
						val inputNBT = stack.tagCompound

						nbtMatcher?.let {
							return it.evaluate(stack, nbtCondition)
						}

						tagList.tagToStack.forEach { tagMapping ->
							if(inputNBT == tagMapping.tag)
								return tagMapping.stack.areCapsCompatible(stack)
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
			if(it.hasTagCompound() && it.tagCompound != null && nbtMatcher == null) {
				hash = 31 * hash + it.tagCompound.hashCode()
			}
		}
		return 31 * hash + Objects.hash(amount, isConsumable, nbtMatcher, nbtCondition)
	}

	override fun equals(other: Any?) =
		this === other || (other is ItemInput && amount == other.amount && isConsumable == other.isConsumable && nbtMatcher == other.nbtMatcher && nbtCondition == other.nbtCondition && inputStacks?.size == other.inputStacks?.size && inputStacks?.mapIndexed { idx, el -> !ItemStack.areItemStacksEqual(el, other.inputStacks!![idx]) }?.any() != true)

	// roz: shouldn't this use areStacksEqualIgnoreQuantity
	override fun equalsIgnoreAmount(input: RecipeInput) =
		this === input || (input is ItemInput && nbtMatcher == input.nbtMatcher && nbtCondition == input.nbtCondition && inputStacks?.size == input.inputStacks?.size && inputStacks?.mapIndexed { idx, el -> !ItemStack.areItemStacksEqual(el, input.inputStacks!![idx]) }?.any() != true)

	override fun toString() =
		when(inputStacks?.size) {
			0, null -> "${amount}x[]"
			1 -> "${amount}x${toStringWithoutQuantity(this.inputStacks!![0])}"
			else -> "${amount}x[${inputStacks!!.joinToString("|") { toStringWithoutQuantity(it) }}]"
		}

	companion object {
		private fun toStringWithoutQuantity(stack: ItemStack) =
			"${stack.item.translationKey}@${stack.itemDamage}"
	}
}
