package org.ender_development.catalyx.api.v1.common.recipes.handlers

import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedInput
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode
import org.ender_development.catalyx.api.v1.common.recipes.components.impl.FluidRecipeInput
import org.ender_development.catalyx.api.v1.common.recipes.components.impl.FluidRecipeOutput
import org.ender_development.catalyx.api.v1.modules.annotations.CatalyxLoadClass

/**
 * [StackHandler] implementation for fluid-type stacks.
 *
 * Handles conversion between [FluidStack] and [FluidRecipeInput]/[FluidRecipeOutput].
 * Self-registers on first reference.
 */
@CatalyxLoadClass
object FluidStackHandler : StackHandler<FluidStack, FluidRecipeInput, FluidRecipeOutput>() {

	override fun canHandle(stack: Any) = stack is FluidStack

	override fun handles(component: RecipeComponent) =
		component is FluidRecipeInput || component is FluidRecipeOutput

	override fun stackToInput(
		stack: FluidStack,
		consumeChance: Double,
		rollMode: RollMode
	) = FluidRecipeInput(
		amount =  stack.amount,
		validFluids = listOf(stack),
		consumeChance = consumeChance,
		rollMode = rollMode
	)

	override fun stackGroupToInput(
		stacks: List<FluidStack>,
		consumeChance: Double,
		rollMode: RollMode
	) = FluidRecipeInput(
		amount = stacks.maxOf { it.amount },
		validFluids = stacks,
		consumeChance = consumeChance,
		rollMode = rollMode
	)

	override fun stackToOutput(
		stack: FluidStack,
		produceChance: Double,
		rollMode: RollMode
	) = FluidRecipeOutput(
		amount = stack.amount,
		fluid = stack,
		produceChance = produceChance,
		rollMode = rollMode
	)

	override fun consumeFromStacks(stacks: MutableList<FluidStack>, resolved: ResolvedInput) {
		if (resolved.amountToConsume == 0) return
		val input = resolved.component as? FluidRecipeInput ?: return

		var remaining = resolved.amountToConsume
		val iterator = stacks.iterator()

		while (iterator.hasNext() && remaining > 0) {
			val stack = iterator.next()
			if (stack !in input.validFluids) continue

			if (stack.amount <= remaining) {
				remaining -= stack.amount
				iterator.remove()
			} else {
				val index = stacks.indexOf(stack)
				stacks[index] = FluidStack(stack.fluid, stack.amount - remaining, stack.tag)
				remaining = 0
			}
		}
	}

	override fun resolvedToStack(resolved: ResolvedOutput): FluidStack? {
		if (resolved.amount == 0) return null
		val output = resolved.component as? FluidRecipeOutput ?: return null
		return output.fluid
	}
}


