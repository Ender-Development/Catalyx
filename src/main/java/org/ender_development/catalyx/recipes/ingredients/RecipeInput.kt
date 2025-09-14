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

	var amount: Int = 1
		protected set
	protected var isConsumable: Boolean = true
	var nbtMatcher: IMatcher? = null
	var nbtCondition: NBTCondition? = null

	var cached: Boolean = false
		private set
	private var hash: Int = 0

	protected var hashCached: Boolean = false

	fun setCached() {
		cached = true
	}

	protected abstract fun copy(): RecipeInput

	fun withAmount(amount: Int): RecipeInput {
		if (this.amount == amount) return this
		this.amount = amount
		cached = false
		return this
	}

	fun setNonConsumable(): RecipeInput {
		if (!isConsumable) return this
		val recipeInput = if (cached) copy() else this
		recipeInput.isConsumable = false
		recipeInput.cached = false
		return recipeInput
	}

	fun setNBTMatchingCondition(condition: NBTCondition, matcher: IMatcher): RecipeInput {
		if (nbtCondition == condition && nbtMatcher == matcher) return this
		val recipeInput = if (cached) copy() else this
		recipeInput.nbtCondition = condition
		recipeInput.nbtMatcher = matcher
		recipeInput.cached = false
		return recipeInput
	}

	fun hasNBTMatchingCondition() = nbtMatcher != null;
	fun getNBTMatcher() = nbtMatcher
	fun getNBTMatchingCondition() = nbtCondition
	fun isNonConsumable() = !isConsumable
	open fun getInputStacks(): List<ItemStack>? = null
	fun getInputFluidStacks(): List<FluidStack>? = null
	fun isOreDict() = false
	fun getOreDict(): Int = -1
	open fun acceptsStack(stack: ItemStack?): Boolean = false
	fun acceptsFluid(stack: FluidStack?): Boolean = false

	protected abstract fun computeHash(): Int

	abstract override fun equals(other: Any?): Boolean

	override fun hashCode(): Int {
		if (!hashCached) {
			hash = computeHash()
			hashCached = true
		}
		return hash
	}

	abstract fun equalsIgnoreAmount(input: RecipeInput): Boolean
}
