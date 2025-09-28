package org.ender_development.catalyx.recipes.ingredients

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import java.util.*

class OreInput : RecipeInput {
	companion object {
		var STANDARD: Short = 0
			private set

		/**
		 * Forces a refresh on every OreInput stack cache
		 * Used for compat with CraftTweaker and GroovyScript
		 */
		internal fun refreshStackCache() =
			STANDARD++
	}

	private var currentStandard: Short = 0
	private val ore: Int
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
			inputStacks = OreDictionary.getOres(OreDictionary.getOreName(ore)).map {
				it.copy().let { copy ->
					copy.count = amount
					copy
				}
			}
		}
		return inputStacks
	}

	override fun isOreDict() =
		true

	override fun getOreDict(): Int =
		ore

	override fun acceptsStack(stack: ItemStack?): Boolean {
		if(stack == null || stack.isEmpty)
			return false

		nbtMatcher?.let { matcher ->
			getInputStacks()?.forEach {
				if(OreDictionary.itemMatches(it, stack, false))
					return matcher.evaluate(stack, nbtCondition)
			}
		}

		return false
	}

	override fun computeHash(): Int =
		Objects.hash(amount, ore, isConsumable, nbtMatcher, nbtCondition)

	override fun equals(other: Any?) =
		this === other || (other is OreInput && amount == other.amount && isConsumable == other.isConsumable && nbtMatcher == other.nbtMatcher && nbtCondition == other.nbtCondition && ore == other.ore)

	override fun equalsIgnoreAmount(input: RecipeInput) =
		this === input || (input is OreInput && nbtMatcher == input.nbtMatcher && nbtCondition == input.nbtCondition && ore == input.ore)

	override fun toString() =
		"${amount}x${OreDictionary.getOreName(ore)}"
}
