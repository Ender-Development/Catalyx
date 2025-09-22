package org.ender_development.catalyx.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.recipes.chance.output.ChancedFluidOutput
import org.ender_development.catalyx.recipes.chance.output.ChancedItemOutput
import org.ender_development.catalyx.recipes.chance.output.ChancedOutputList
import org.ender_development.catalyx.recipes.ingredients.RecipeInput
import org.ender_development.catalyx.recipes.ingredients.RecipeInputCache

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
	}

	fun copy() =
		Recipe(inputs, outputs, chancedOutputs, fluidInputs, fluidOutputs, chancedFluidOutput, duration, energyPerTick, hidden, recipeCategory)

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
	 * Takes into account any specific output limiters, ie macerator slots, to trim down the output list
	 * Trims from chanced outputs first, then regular outputs
	 *
	 * @param outputLimit The limit on the number of outputs, -1 for disabled.
	 * @return A Pair of recipe outputs and chanced outputs, limited by some factor
	 */
	fun getItemAndChanceOutputs(outputLimit: Int): Pair<List<ItemStack>, List<ChancedItemOutput>> {
		TODO()
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
		TODO()
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
		return fluidInputs.any {
			it.getInputFluidStack()?.let {
				it.amount > 0
			} != false
		}
	}

	fun hasInputFluid(fluid: FluidStack): Boolean =
		fluidInputs.any { it.getInputFluidStack()?.isFluidEqual(fluid) == true }
}

