package org.ender_development.catalyx.recipes.ingredients

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import org.jetbrains.annotations.ApiStatus
import java.util.*
import java.util.stream.Collectors

class OreInput : RecipeInput {
	companion object {
		var STANDARD: Short = 0

		/**
		 * Forces a refresh on every OreInput stack cache
		 * Used for compat with CraftTweaker and GroovyScript
		 */
		@ApiStatus.Internal
		fun refreshStackCache() = STANDARD++
	}

	private var currentStandard: Short = 0
	private var ore: Int
	private var inputStacks: List<ItemStack>? = null

	constructor(oreDict: String, amount: Int) {
		this.ore = OreDictionary.getOreID(oreDict)
		this.amount = amount
	}

	constructor(oreDict: String) : this(oreDict, 1)
	constructor(oreDictID: Int, amount: Int) {
		this.ore = oreDictID
		this.amount = amount
	}

	override fun copy(): OreInput {
		val copy = OreInput(ore, amount)
		copy.isConsumable = this.isConsumable
		copy.nbtMatcher = this.nbtMatcher
		copy.nbtCondition = this.nbtCondition
		return copy
	}

	/**
	 * The items returned here are not updated after its first call, unless standard is changed,
	 * so they are not suitable for use while recipes are being processed and the OreDicts being modified.
	 */
	override fun getInputStacks(): List<ItemStack>? {
		// Standard forces a refresh of the input stack cache.
		// Used in GroovyScript Reload, and upon Load Complete to fix unreliable behaviour with CT and GS scripts.
		if(inputStacks != null || currentStandard != STANDARD) {
			currentStandard = STANDARD
			inputStacks = (OreDictionary.getOres(OreDictionary.getOreName(ore))).stream().map {
				val copy = it.copy()
				copy.count = amount
				copy
			}.collect(Collectors.toList<ItemStack>())
		}
		return inputStacks
	}

	override fun isOreDict() =
		true

	override fun getOreDict(): Int =
		ore

	override fun acceptsStack(stack: ItemStack?): Boolean {
		if(stack == null || stack.isEmpty) return false
		getInputStacks()?.forEach {
			if(OreDictionary.itemMatches(it, stack, false))
				return nbtMatcher?.evaluate(stack, nbtCondition) == true
		}
		return false
	}

	override fun computeHash(): Int =
		Objects.hash(amount, ore, isConsumable, nbtMatcher, nbtCondition)

	override fun equals(other: Any?): Boolean {
		if(this == other) return true
		if(other !is OreInput) return false
		if(this.amount != other.amount || this.isConsumable != other.isConsumable) return false
		if(!Objects.equals(this.nbtMatcher, other.nbtMatcher)) return false
		if(!Objects.equals(this.nbtCondition, other.nbtCondition)) return false
		return ore == other.ore
	}

	override fun equalsIgnoreAmount(input: RecipeInput): Boolean {
		if(this == input) return true
		if(input !is OreInput) return false
		if(!Objects.equals(this.nbtMatcher, input.nbtMatcher)) return false
		if(!Objects.equals(this.nbtCondition, input.nbtCondition)) return false
		return ore == input.ore
	}

	override fun toString(): String =
		"${amount}x${OreDictionary.getOreName(ore)}"
}
