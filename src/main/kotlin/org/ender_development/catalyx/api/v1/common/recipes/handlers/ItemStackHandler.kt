package org.ender_development.catalyx.api.v1.common.recipes.handlers

import net.minecraft.item.ItemStack
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedInput
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode
import org.ender_development.catalyx.api.v1.common.recipes.components.impl.ItemRecipeInput
import org.ender_development.catalyx.api.v1.common.recipes.components.impl.ItemRecipeOutput
import org.ender_development.catalyx.api.v1.modules.annotations.CatalyxLoadClass

/**
 * [StackHandler] implementation for item-type stacks.
 *
 * Handles conversion between [ItemStack] and [ItemRecipeInput]/[ItemRecipeOutput].
 * Self-registers on first reference.
 */
@CatalyxLoadClass
object ItemStackHandler : StackHandler<ItemStack, ItemRecipeInput, ItemRecipeOutput>() {

	override fun canHandle(stack: Any) = stack is ItemStack

	override fun handles(component: RecipeComponent) =
		component is ItemRecipeInput || component is ItemRecipeOutput

	override fun stackToInput(
		stack: ItemStack,
		consumeChance: Double,
		rollMode: RollMode
	) = ItemRecipeInput(
		amount = stack.count,
		validItems = listOf(stack),
		consumeChance = consumeChance,
		rollMode = rollMode
	)

	override fun stackGroupToInput(
		stacks: List<ItemStack>,
		consumeChance: Double,
		rollMode: RollMode
	) = ItemRecipeInput(
		amount = stacks.maxOf { it.count },
		validItems = stacks,
		consumeChance = consumeChance,
		rollMode = rollMode
	)

	override fun stackToOutput(
		stack: ItemStack,
		produceChance: Double,
		rollMode: RollMode
	) = ItemRecipeOutput(
		amount = stack.count,
		item = stack,
		produceChance = produceChance,
		rollMode = rollMode
	)

	override fun consumeFromStacks(stacks: MutableList<ItemStack>, resolved: ResolvedInput) {
		if (resolved.amountToConsume == 0) return
		val input = resolved.component as? ItemRecipeInput ?: return

		var remaining = resolved.amountToConsume
		val iterator = stacks.iterator()

		while (iterator.hasNext() && remaining > 0) {
			val stack = iterator.next()
			if (stack !in input.validItems) continue

			if (stack.count <= remaining) {
				remaining -= stack.count
				iterator.remove()
			} else {
				val index = stacks.indexOf(stack)
				stacks[index] = ItemStack(stack.item, stack.count - remaining, stack.metadata, stack.tagCompound)
				remaining = 0
			}
		}
	}

	override fun resolvedToStack(resolved: ResolvedOutput): ItemStack? {
		if (resolved.amount == 0) return null
		val output = resolved.component as? ItemRecipeOutput ?: return null
		return output.item
	}
}


