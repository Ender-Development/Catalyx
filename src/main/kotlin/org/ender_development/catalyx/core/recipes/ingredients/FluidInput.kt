package org.ender_development.catalyx.core.recipes.ingredients

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.*

class FluidInput : RecipeInput {
	companion object {
		/**
		 * The Fluid registered to the fluidName on game load might not be the same Fluid after
		 * loading the world, but will still have the same fluidName.
		 */
		fun areFluidsEqual(fluid1: FluidStack?, fluid2: FluidStack?) =
			fluid1?.fluid?.name == fluid2?.fluid?.name
	}

	private val inputStack: FluidStack

	constructor(fluid: Fluid, amount: Int) : this(FluidStack(fluid, amount))

	constructor(inputStack: FluidStack) {
		this.inputStack = inputStack
		this.amount = inputStack.amount
	}

	constructor(inputStack: FluidStack, amount: Int) {
		this.inputStack = inputStack.copy()
		this.inputStack.amount = inputStack.amount
		this.amount = amount
	}

	override fun copy(): RecipeInput {
		val copy = FluidInput(inputStack, amount)
		copy.isConsumable = this.isConsumable
		copy.nbtMatcher = this.nbtMatcher
		copy.nbtCondition = this.nbtCondition
		return copy
	}

	override fun getInputFluidStack(): FluidStack =
		inputStack

	override fun acceptsFluid(stack: FluidStack?): Boolean {
		if(stack == null || stack.amount == 0)
			return false

		if(!areFluidsEqual(inputStack, stack))
			return false

		return nbtMatcher?.evaluate(stack, nbtCondition) ?: FluidStack.areFluidStackTagsEqual(inputStack, stack)
	}

	override fun computeHash(): Int =
		Objects.hash(this.inputStack.fluid.name, this.amount, this.nbtMatcher, this.nbtCondition, if(this.nbtMatcher == null) this.inputStack.tag else 0)

	override fun equals(other: Any?) =
		this === other || (other is FluidInput && amount == other.amount && isConsumable == other.isConsumable && nbtMatcher == other.nbtMatcher && nbtCondition == other.nbtCondition && areFluidsEqual(inputStack, other.inputStack) && (nbtMatcher != null || FluidStack.areFluidStackTagsEqual(inputStack, other.inputStack)))

	override fun equalsIgnoreAmount(input: RecipeInput) =
		this === input || (input is FluidInput && nbtMatcher == input.nbtMatcher && nbtCondition == input.nbtCondition && areFluidsEqual(inputStack, input.inputStack) && (nbtMatcher != null || FluidStack.areFluidStackTagsEqual(inputStack, input.inputStack)))

	override fun toString() =
		"${amount}x${inputStack.unlocalizedName}"
}
