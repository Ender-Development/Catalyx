package org.ender_development.catalyx.api.v1.common.recipes.components.impl

import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.extensions.areStacksEqualIgnoreQuantity
import org.ender_development.catalyx.api.v1.common.extensions.nbtString
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeInput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode

/**
 * Internal [RecipeInput] implementation for item-type resources.
 *
 * @property amount The number of items required. Must be > 0.
 * @property validItems The list of item ids that satisfy this input. Must not be empty.
 * @property consumeChance The probability this input is consumed. Defaults to 1.0.
 * @property rollMode How consume chance is rolled. Defaults to [RollMode.PER_STACK].
 */
data class ItemRecipeInput(
	override val amount: Int,
	val validItems: List<ItemStack>,
	override val consumeChance: Double = 1.0,
	override val rollMode: RollMode = RollMode.PER_STACK
) : RecipeInput() {

	init {
		require(amount > 0) { "ItemRecipeInput amount must be > 0" }
		require(consumeChance in 0.0..1.0) { "consumeChance must be in [0.0, 1.0]" }
		require(validItems.isNotEmpty()) { "ItemRecipeInput must have at least one valid item" }
	}

	/**
	 * Returns true if [provided] is an [ItemRecipeInput] whose item is in [validItems]
	 * and whose amount is >= the required [amount].
	 */
	override fun matches(provided: RecipeComponent): Boolean =
		provided is ItemRecipeInput && validItems.any {
			// TODO: This is totally winged, pls check if this works. :c
			// roz - ^, you can use IS#areStacksEqualIgnoreQuantity, IS#canMergeWith and IS#areItem[...] functions instead of reinventing the wheel
			provided.validItems.any { other ->
				if (it.item != other.item) return false
				if (it.itemDamage != other.itemDamage) return false
				(it.tagCompound == null || it.tagCompound == other.tagCompound) && it.areCapsCompatible(other)

			}
		} && provided.amount >= amount

	/**
	 * Returns a copy of this input with [other]'s amount added.
	 * Used to aggregate multiple item stacks of the same type before recipe matching.
	 *
	 * @throws IllegalArgumentException if [other] is not an [ItemRecipeInput].
	 */
	override operator fun plus(other: RecipeInput): ItemRecipeInput {
		require(other is ItemRecipeInput) {
			"Cannot add ItemRecipeInput and ${other::class.java.simpleName}"
		}
		return copy(amount = amount + other.amount)
	}

	override fun toString(): String =
		"ITEM:[${validItems.map(ItemStack::nbtString).sorted().joinToString(",")}]:$amount:$consumeChance:$rollMode"
}

