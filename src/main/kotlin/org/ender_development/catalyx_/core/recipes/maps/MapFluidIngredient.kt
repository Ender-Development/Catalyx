package org.ender_development.catalyx_.core.recipes.maps

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx_.core.recipes.ingredients.RecipeInput
import java.util.*

class MapFluidIngredient : AbstractMapIngredient {
	val fluid: Fluid
	val tag: NBTTagCompound?

	constructor(fluidInput: RecipeInput) : this(fluidInput.getInputFluidStack()!!)

	constructor(fluidStack: FluidStack) {
		this.fluid = fluidStack.fluid
		this.tag = fluidStack.tag
	}

	// the Fluid registered to the fluidName on game load might not be the same Fluid after loading the world,
	// but will still have the same fluidName.
	override fun hash() =
		Objects.hash(fluid.name, tag)

	// the Fluid registered to the fluidName on game load might not be the same Fluid after loading the world,
	// but will still have the same fluidName.
	override fun equals(other: Any?) =
		this === other || (other is MapFluidIngredient && fluid.name == other.fluid.name && tag == other.tag)

	override fun toString(): String =
		"MapFluidIngredient{{fluid=${fluid.name}} {tag=$tag}"
}
