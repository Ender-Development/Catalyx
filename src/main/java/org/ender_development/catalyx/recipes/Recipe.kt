package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.recipes.chance.output.ChancedFluidOutput
import org.ender_development.catalyx.recipes.chance.output.ChancedItemOutput
import org.ender_development.catalyx.recipes.chance.output.ChancedOutputList
import org.ender_development.catalyx.recipes.ingredients.RecipeInput
import org.ender_development.catalyx.recipes.ingredients.RecipeInputCache
import org.ender_development.catalyx.utils.IItemStackHash
import org.ender_development.catalyx.utils.extensions.copyOf

class Recipe(
	inputs: List<RecipeInput?>,
	val outputs: List<ItemStack>,
	val chancedOutputs: ChancedOutputList<ItemStack, ChancedItemOutput>,
	fluidInputs: List<RecipeInput?>,
	val fluidOutputs: List<FluidStack>,
	val chancedFluidOutput: ChancedOutputList<FluidStack, ChancedFluidOutput>,
	val duration: Int,
	val energyPerTick: Long,
	val hidden: Boolean,
	val recipeCategory: RecipeCategory
) {
	val inputs = RecipeInputCache.deduplicateInputs(inputs)
	val fluidInputs = RecipeInputCache.deduplicateInputs(fluidInputs)
	val hashCode = makeHashCode()

	companion object {
		/**
		 * Trims the recipe outputs, chanced outputs, and fluid outputs based on the performing TileEntity's trim limit.
		 *
		 * @param currentRecipe  The recipe to perform the output trimming upon
		 * @param recipeMap      The RecipeMap that the recipe is from
		 * @param itemTrimLimit  The Limit to which item outputs should be trimmed to, -1 for no trimming
		 * @param fluidTrimLimit The Limit to which fluid outputs should be trimmed to, -1 for no trimming
		 * @return A new Recipe whose outputs have been trimmed.
		 */
		fun trimRecipeOutput(currentRecipe: Recipe, recipeMap: RecipeMap<*>, itemTrimLimit: Int, fluidTrimLimit: Int): Recipe {
			// Fast return early if no trimming desired
			if(itemTrimLimit == -1 && fluidTrimLimit == -1)
				return currentRecipe
			val currentRecipe = currentRecipe.copy()
			val builder = RecipeBuilder(currentRecipe, recipeMap)
			TODO("Builder Stuff that needs to be implemented")

			val recipeOutputs = currentRecipe.getItemAndChanceOutputs(itemTrimLimit)
			TODO("Set builder outputs and chanced outputs")

			val recipeFluidOutputs = currentRecipe.getFluidAndChanceOutputs(fluidTrimLimit)
			TODO("Set builder fluid outputs and chanced fluid outputs")

			TODO("Build and return new recipe")
			return currentRecipe
		}

		fun hashFluidList(fluidList: List<RecipeInput>): Int {
			var hash = 1
			fluidList.forEach {
				hash = 31 * hash + it.hashCode()
			}
			return hash
		}

		fun hashItemList(itemList: List<RecipeInput>): Int {
			var hash = 1
			itemList.forEach {
				when {
					!it.isOreDict() -> it.getInputStacks()?.forEach { stack -> hash = 31 * hash + IItemStackHash.comparingAll.hashCode(stack) }
					else -> hash = 31 * hash + it.getOreDict()
				}
			}
			return hash
		}
	}

	fun copy() =
		Recipe(inputs, outputs, chancedOutputs, fluidInputs, fluidOutputs, chancedFluidOutput, duration, energyPerTick, hidden, recipeCategory)

	override fun equals(other: Any?) =
		this === other || (other is Recipe && hasSameInputs(other) && hasSameFluidInputs(other))

	override fun toString() =
		"Recipe{inputs=$inputs; outputs=$outputs; chancedOutputs=$chancedOutputs; fluidInputs=$fluidInputs; fluidOutputs=$fluidOutputs; chancedFluidOutput=$chancedFluidOutput; duration=$duration; energyPerTick=$energyPerTick; hidden=$hidden}"

	override fun hashCode() =
		hashCode

	private fun makeHashCode(): Int {
		var hash = 31 * hashItemList(inputs)
		hash = 31 * hash + hashFluidList(fluidInputs)
		return hash
	}

	private fun hasSameInputs(other: Recipe): Boolean {
		val otherStackList = ObjectArrayList<ItemStack>(other.inputs.size)

		other.inputs.forEach {
			otherStackList.addAll(it.getInputStacks()!!)
		}

		if(!matchesItems(otherStackList).first)
			return false

		val thisStackList = ObjectArrayList<ItemStack>(inputs.size)

		inputs.forEach {
			thisStackList.addAll(it.getInputStacks()!!)
		}

		return other.matchesItems(thisStackList).first
	}

	private fun hasSameFluidInputs(other: Recipe): Boolean {
		val otherFluidList = ObjectArrayList<FluidStack>(other.fluidInputs.size)

		other.fluidInputs.forEach {
			otherFluidList.add(it.getInputFluidStack()!!)
		}

		if(!matchesFluids(otherFluidList).first)
			return false

		val thisFluidList = ObjectArrayList<FluidStack>(fluidInputs.size)

		fluidInputs.forEach {
			thisFluidList.add(it.getInputFluidStack()!!)
		}

		return other.matchesFluids(thisFluidList).first
	}

	private fun matchesItems(inputs: List<ItemStack?>): Pair<Boolean, IntArray> {
		val itemAmountInSlot = IntArray(inputs.size)
		var indexed = 0
		this.inputs.forEach {
			var ingredientAmount = it.amount
			for(i in inputs.indices) {
				val inputStack = inputs[i]
				if(i == indexed) {
					++indexed
					itemAmountInSlot[i] = if(inputStack?.isEmpty != false) 0 else inputStack.count
				}

				if(inputStack?.isEmpty != false || !it.acceptsStack(inputStack))
					continue

				val itemAmountToConsume = itemAmountInSlot[i].coerceAtMost(ingredientAmount)
				ingredientAmount -= itemAmountToConsume

				if(!it.isNonConsumable())
					itemAmountInSlot[i] -= itemAmountToConsume

				if(ingredientAmount == 0)
					break
			}
			if(ingredientAmount > 0)
				return false to itemAmountInSlot
		}
		val returnItemAmountInSlot = IntArray(indexed)
		System.arraycopy(itemAmountInSlot, 0, returnItemAmountInSlot, 0, indexed)
		return true to returnItemAmountInSlot
	}

	private fun matchesFluids(inputs: List<FluidStack?>): Pair<Boolean, IntArray> {
		val fluidAmountInTank = IntArray(inputs.size)
		var indexed = 0
		fluidInputs.forEach {
			var fluidAmount = it.amount
			for(i in inputs.indices) {
				val tankFluid = inputs[i]
				if(i == indexed) {
					++indexed
					fluidAmountInTank[i] = tankFluid?.amount ?: 0
				}

				if(tankFluid == null || !it.acceptsFluid(tankFluid))
					continue

				val fluidAmountToConsume = fluidAmountInTank[i].coerceAtMost(fluidAmount)
				fluidAmount -= fluidAmountToConsume

				if(!it.isNonConsumable())
					fluidAmountInTank[i] -= fluidAmountToConsume

				if(fluidAmount == 0)
					break
			}
			if(fluidAmount > 0)
				return false to fluidAmountInTank
		}
		val returnFluidAmountInTank = IntArray(indexed)
		System.arraycopy(fluidAmountInTank, 0, returnFluidAmountInTank, 0, indexed)
		return true to returnFluidAmountInTank
	}

	/**
	 * Returns all outputs from the recipe.
	 * This is where Chanced Outputs for the recipe are calculated.
	 * The Recipe should be trimmed by calling [Recipe.getItemAndChanceOutputs] before calling this method,
	 * if trimming is required.
	 *
	 * @param recipeTier  The Tier of the Recipe, used for chanced output calculation
	 * @param machineTier The Tier of the Machine, used for chanced output calculation
	 * @param recipeMap   The RecipeMap that the recipe is being performed upon, used for chanced output calculation
	 * @return A list of all resulting ItemStacks from the recipe, after chance has been applied to any chanced outputs
	 */
	fun getResultItemOutputs(recipeTier: Int, machineTier: Int, recipeMap: RecipeMap<*>): List<ItemStack> {
		val outputs = outputs.toMutableList()
		val boostFunction = recipeMap.chanceBoostFunction
		val chancedOutputList = chancedOutputs.roll(boostFunction, recipeTier, machineTier)

		val resultChanced = mutableListOf<ItemStack>()
		chancedOutputList?.forEach {
			val stackToAdd = it.ingredient.copy()
			resultChanced.forEach { stackInList ->
				val insertable = stackInList.maxStackSize - stackInList.count
				if(insertable > 0 && ItemHandlerHelper.canItemStacksStack(stackInList, stackToAdd)) {
					val insert = stackToAdd.count.coerceAtMost(insertable)
					stackInList.grow(insert)
					stackToAdd.shrink(insert)
					if(stackToAdd.isEmpty)
						return@forEach
				}
			}
			if(!stackToAdd.isEmpty)
				resultChanced.add(stackToAdd)
		} ?: return outputs

		outputs.addAll(resultChanced)
		return outputs
	}

	/**
	 * Returns all outputs from the recipe.
	 * This is where Chanced Outputs for the recipe are calculated.
	 * The Recipe should be trimmed by calling [Recipe.getFluidAndChanceOutputs] before calling this method,
	 * if trimming is required.
	 *
	 * @param recipeTier  The Tier of the Recipe, used for chanced output calculation
	 * @param machineTier The Tier of the Machine, used for chanced output calculation
	 * @param recipeMap   The RecipeMap that the recipe is being performed upon, used for chanced output calculation
	 * @return A list of all resulting ItemStacks from the recipe, after chance has been applied to any chanced outputs
	 */
	fun getResultFluidOutputs(recipeTier: Int, machineTier: Int, recipeMap: RecipeMap<*>): List<FluidStack> {
		val outputs = fluidOutputs.toMutableList()
		val boostFunction = recipeMap.chanceBoostFunction
		val chancedOutputList = chancedFluidOutput.roll(boostFunction, recipeTier, machineTier)

		val resultChanced = mutableListOf<FluidStack>()
		chancedOutputList?.forEach {
			val stackToAdd = it.ingredient
			if(resultChanced.any { stackInList ->
					val insertable = stackInList.amount
					if(insertable > 0 && stackInList.fluid === stackToAdd.fluid) {
						stackInList.amount += insertable
						true
					}
					false
				})
				resultChanced.add(stackToAdd.copy())
		} ?: return outputs

		outputs.addAll(resultChanced)
		return outputs
	}

	/**
	 * Returns the maximum possible recipe outputs from a recipe, divided into regular and chanced outputs
	 * Takes into account any specific output limiters, i.e. macerator slots, to trim down the output list
	 * Trims from chanced outputs first, then regular outputs
	 *
	 * @param outputLimit The limit on the number of outputs, -1 for disabled.
	 * @return A Pair of recipe outputs and chanced outputs, limited by some factor
	 */
	fun getItemAndChanceOutputs(outputLimit: Int): Pair<List<ItemStack>, List<ChancedItemOutput>> {
		val outputs = mutableListOf<ItemStack>()
		var chancedOutputs = chancedOutputs.chancedElements.toMutableList()
		when {
			// No limiting
			outputLimit == -1 -> outputs.addAll(this.outputs)

			// If just the regular outputs would satisfy the outputLimit
			this.outputs.size >= outputLimit -> {
				outputs.addAll(this.outputs.subList(0, this.outputs.size.coerceAtMost(outputLimit)))
				chancedOutputs.clear()
			}

			// If the regular outputs and chanced outputs are required to satisfy the outputLimit
			!this.outputs.isEmpty() && this.outputs.size + chancedOutputs.size >= outputLimit -> {
				outputs.addAll(this.outputs)
				val remainingSpace = outputLimit - this.outputs.size
				chancedOutputs = chancedOutputs.subList(0, chancedOutputs.size.coerceAtMost(remainingSpace))
			}

			// There are only chanced outputs to satisfy the outputLimit
			this.outputs.isEmpty() -> chancedOutputs = chancedOutputs.subList(0, chancedOutputs.size.coerceAtMost(outputLimit))

			// The number of outputs + chanced outputs is lower than the trim number, so just add everything
			// Chanced outputs are taken care of in the original copy
			else -> outputs.addAll(this.outputs)
		}
		return outputs.copyOf() to chancedOutputs
	}

	/**
	 * Returns the maximum possible recipe outputs from a recipe, divided into regular and chanced outputs
	 * Takes into account any specific output limiters, ie macerator slots, to trim down the output list
	 * Trims from chanced outputs first, then regular outputs
	 *
	 * @param outputLimit The limit on the number of outputs, -1 for disabled.
	 * @return A Pair of recipe outputs and chanced outputs, limited by some factor
	 */
	fun getFluidAndChanceOutputs(outputLimit: Int): Pair<List<FluidStack>, List<ChancedFluidOutput>> {
		val outputs = mutableListOf<FluidStack>()
		var chancedOutputs = chancedFluidOutput.chancedElements.toMutableList()
		when {
			// No limiting
			outputLimit == -1 -> outputs.addAll(fluidOutputs)

			// If just the regular outputs would satisfy the outputLimit
			fluidOutputs.size >= outputLimit -> {
				outputs.addAll(fluidOutputs.subList(0, fluidOutputs.size.coerceAtMost(outputLimit)))
				chancedOutputs.clear()
			}

			// If the regular outputs and chanced outputs are required to satisfy the outputLimit
			!fluidOutputs.isEmpty() && fluidOutputs.size + chancedOutputs.size >= outputLimit -> {
				outputs.addAll(fluidOutputs)
				val remainingSpace = outputLimit - fluidOutputs.size
				chancedOutputs = chancedOutputs.subList(0, chancedOutputs.size.coerceAtMost(remainingSpace))
			}

			// There are only chanced outputs to satisfy the outputLimit
			fluidOutputs.isEmpty() -> chancedOutputs = chancedOutputs.subList(0, chancedOutputs.size.coerceAtMost(outputLimit))

			// The number of outputs + chanced outputs is lower than the trim number, so just add everything
			// Chanced outputs are taken care of in the original copy
			else -> outputs.addAll(fluidOutputs)
		}
		return outputs.copyOf() to chancedOutputs
	}

	/**
	 * Returns a list of every possible ItemStack output from a recipe, including all possible chanced outputs.
	 *
	 * @return A List of ItemStack outputs from the recipe, including all chanced outputs
	 */
	fun getAllItemOutputs() =
		// the plus() operator fun for iterables/collections is cursed and I don't know if I want to keep this code like this or revert it to what Ender wrote
		outputs + chancedOutputs.chancedElements.map { it.ingredient.copy() }

	/**
	 * Returns a list of every possible FluidStack output from a recipe, including all possible chanced outputs.
	 *
	 * @return A List of FluidStack outputs from the recipe, including all chanced outputs
	 */
	fun getAllFluidOutputs() =
		fluidOutputs + chancedFluidOutput.chancedElements.map { it.ingredient.copy() }

	fun hasValidInputsForDisplay(): Boolean {
		inputs.forEach {
			if(it.isOreDict())
				if(OreDictionary.getOres(OreDictionary.getOreName(it.getOreDict())).any { s -> !s.isEmpty })
					return true
				else if(it.getInputStacks()?.any { s -> !s.isEmpty } == true)
					return true
		}
		return fluidInputs.any { input ->
			input.getInputFluidStack()?.let {
				it.amount > 0
			} != false
		}
	}

	fun hasInputFluid(fluid: FluidStack): Boolean =
		fluidInputs.any { it.getInputFluidStack()?.isFluidEqual(fluid) == true }

	/**
	 * This methods aim to verify if the current recipe matches the given inputs according to matchingMode mode.
	 *
	 * @param consumeIfSuccessful if true will consume the inputs of the recipe.
	 * @param inputs              Items input or [emptyList] if none.
	 * @param fluidInputs         Fluids input or [emptyList] if none.
	 * @return true if the recipe matches the given inputs false otherwise.
	 */
	fun matches(consumeIfSuccessful: Boolean, inputs: MutableList<ItemStack?>, fluidInputs: MutableList<FluidStack?>): Boolean {
		if(inputs.isEmpty() && fluidInputs.isEmpty())
			return false

		val (fluidsMatch, fluidAmountInTank) = matchesFluids(fluidInputs)
		if(!fluidsMatch)
			return false

		val (itemsMatch, itemAmountInSlot) = matchesItems(inputs)
		if(!itemsMatch)
			return false

		if(!consumeIfSuccessful)
			return true

		// roz: these loops are pointless? we alrways return `true` no matter what happens here.
		fluidInputs.forEachIndexed { index, fluidStack ->
			val fluidAmount = fluidAmountInTank[index]
			if(fluidStack == null || fluidStack.amount == fluidAmount)
				return@forEachIndexed

			fluidStack.amount = fluidAmount

			if(fluidStack.amount == 0)
				fluidInputs[index] = null
		}

		inputs.forEachIndexed { index, itemStack ->
			val itemAmount = itemAmountInSlot[index]
			if(itemStack?.isEmpty != false || itemStack.count == itemAmount)
				return@forEachIndexed

			itemStack.count = itemAmount

			// if(itemStack.count == 0), analogous to fluidInputs?
		}

		return true
	}

	fun matches(consumeIfSuccessful: Boolean, inputs: IItemHandlerModifiable, fluidInputs: IFluidHandler): Boolean {
		TODO()
	}
}

