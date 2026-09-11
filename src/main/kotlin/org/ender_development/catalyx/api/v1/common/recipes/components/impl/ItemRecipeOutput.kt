package org.ender_development.catalyx.api.v1.common.recipes.components.impl

import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.extensions.nbtString
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode

/**
 * Internal [RecipeOutput] implementation for item-type resources.
 *
 * @property amount The base number of items to produce. Must be > 0.
 * @property item The id of the item to produce.
 * @property produceChance The probability this output is produced. Must be in [0.01, 1.0].
 * @property rollMode How produce chance is rolled. Defaults to [RollMode.PER_STACK].
 */
data class ItemRecipeOutput(
	override val amount: Int,
	val item: ItemStack,
	override val produceChance: Double = 1.0,
	override val rollMode: RollMode = RollMode.PER_STACK
) : RecipeOutput() {

	init {
		require(amount > 0) { "ItemRecipeOutput amount must be > 0" }
		require(produceChance in 0.01..1.0) { "produceChance must be in [0.01, 1.0]" }
	}

	/**
	 * Returns a copy of this output with [other]'s amount added.
	 * Used to combine multiple outputs of the same item type after chance resolution.
	 *
	 * @throws IllegalArgumentException if [other] is not an [ItemRecipeOutput].
	 */
	override operator fun plus(other: RecipeOutput): ItemRecipeOutput {
		require(other is ItemRecipeOutput) {
			"Cannot add ItemRecipeOutput and ${other::class.java.simpleName}"
		}
		return copy(amount = amount + other.amount)
	}

	override fun toString() =
		"ITEM:${item.nbtString()}:$amount:$produceChance:$rollMode"
}
