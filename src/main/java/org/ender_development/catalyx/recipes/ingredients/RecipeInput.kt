package org.ender_development.catalyx.recipes.ingredients

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.recipes.ingredients.nbt.IMatcher
import org.ender_development.catalyx.recipes.ingredients.nbt.NBTCondition

abstract class RecipeInput {
	companion object {
		fun writeToNBT(input: RecipeInput): NBTTagCompound {
			TODO()
		}

		fun readFromNBT(tag: NBTTagCompound): RecipeInput {
			TODO()
		}
	}

	var amount = 1
		protected set

	var isConsumable = true
		protected set

	var nbtMatcher: IMatcher? = null
	var nbtCondition: NBTCondition? = null

	var cached = false
		private set

	protected var hashCached = false
	private var hash = 0

	fun setCached() {
		cached = true
	}

	protected abstract fun copy(): RecipeInput

	fun withAmount(amount: Int): RecipeInput {
		if(this.amount == amount)
			return this

		this.amount = amount
		cached = false
		return this
	}

	fun setNonConsumable(): RecipeInput {
		if(!isConsumable)
			return this

		val recipeInput = if(cached) copy() else this
		recipeInput.isConsumable = false
		recipeInput.cached = false
		return recipeInput
	}

	fun setNBTMatchingCondition(condition: NBTCondition, matcher: IMatcher): RecipeInput {
		if(nbtCondition == condition && nbtMatcher == matcher)
			return this

		val recipeInput = if(cached) copy() else this
		recipeInput.nbtCondition = condition
		recipeInput.nbtMatcher = matcher
		recipeInput.cached = false
		return recipeInput
	}

	fun hasNBTMatchingCondition() =
		nbtMatcher != null

	fun isNonConsumable() =
		!isConsumable

	open fun getInputStacks(): List<ItemStack>? =
		null

	open fun getInputFluidStack(): FluidStack? =
		null

	open fun isOreDict() =
		false

	open fun getOreDict() =
		-1

	open fun acceptsStack(stack: ItemStack?) =
		false

	open fun acceptsFluid(stack: FluidStack?) =
		false

	protected abstract fun computeHash(): Int

	abstract override fun equals(other: Any?): Boolean

	override fun hashCode(): Int {
		if(!hashCached) {
			hash = computeHash()
			hashCached = true
		}
		return hash
	}

	abstract fun equalsIgnoreAmount(input: RecipeInput): Boolean
}
