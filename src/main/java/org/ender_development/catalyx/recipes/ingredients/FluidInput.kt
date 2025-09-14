package org.ender_development.catalyx.recipes.ingredients

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.*

class FluidInput : RecipeInput {
	companion object {
		/**
		 * The Fluid registered to the fluidName on game load might not be the same Fluid after
		 * loading the world, but will still have the same fluidName.
		 */
		fun areFluidsEqual(fluid1: FluidStack?, fluid2: FluidStack?): Boolean =
			fluid1?.fluid?.name.equals(fluid2?.fluid?.name)
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
		if(stack == null || stack.amount == 0) return false
		if(!areFluidsEqual(inputStack, stack)) return false
		return if(nbtMatcher == null) FluidStack.areFluidStackTagsEqual(inputStack, stack) else nbtMatcher!!.evaluate(stack, nbtCondition)
	}

	override fun computeHash(): Int =
		Objects.hash(this.inputStack.fluid.name, this.amount, this.nbtMatcher, this.nbtCondition, if(this.nbtMatcher == null) this.inputStack.tag else 0)

	override fun equals(other: Any?): Boolean {
		if(this == other) return true
		if(other !is FluidInput) return false
		if(this.amount != other.amount || this.isConsumable != other.isConsumable) return false
		if(!Objects.equals(this.nbtMatcher, other.nbtMatcher)) return false
		if(!Objects.equals(this.nbtCondition, other.nbtCondition)) return false
		return areFluidsEqual(this.inputStack, other.inputStack) &&
				(this.nbtMatcher != null || FluidStack.areFluidStackTagsEqual(this.inputStack, other.inputStack))
	}

	override fun equalsIgnoreAmount(input: RecipeInput): Boolean {
		if(this == input) return true
		if(input !is FluidInput) return false
		if(!Objects.equals(this.nbtMatcher, input.nbtMatcher)) return false
		if(!Objects.equals(this.nbtCondition, input.nbtCondition)) return false
		return areFluidsEqual(this.inputStack, input.inputStack) &&
				(this.nbtMatcher != null || FluidStack.areFluidStackTagsEqual(this.inputStack, input.inputStack))
	}

	override fun toString(): String =
		"${amount}x${inputStack.unlocalizedName}"
}
