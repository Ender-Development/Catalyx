package org.ender_development.catalyx.recipes.maps

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.recipes.ingredients.RecipeInput
import java.util.*

class MapFluidIngredient : AbstractMapIngredient {
	val fluid: Fluid
	val tag: NBTTagCompound?

	constructor(fluidInput: RecipeInput) : this(fluidInput.getInputFluidStack()!!)

	constructor(fluidStack: FluidStack) {
		this.fluid = fluidStack.fluid
		this.tag = fluidStack.tag
	}

	override fun hash(): Int {
		// the Fluid registered to the fluidName on game load might not be the same Fluid after loading the world, but
		// will still have the same fluidName.
		val hash = 31 + fluid.name.hashCode()
		if(tag != null) {
			return 31 * hash + tag.hashCode()
		}
		return hash
	}

	override fun equals(other: Any?): Boolean {
		if(super.equals(other)) {
			val other = other as MapFluidIngredient
			// the Fluid registered to the fluidName on game load might not be the same Fluid after loading the world,
			// but will still have the same fluidName.
			if(this.fluid.name == other.fluid.name) {
				return Objects.equals(tag, other.tag)
			}
		}
		return false
	}

	override fun toString(): String =
		"MapFluidIngredient{{fluid=${fluid.name}} {tag=$tag}"
}
