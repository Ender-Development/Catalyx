package org.ender_development.catalyx.recipe

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

interface IRecipeHandler {

	/**
	 * Returns the list of recipes of this handler.
	 */
	val recipes: MutableList<IRecipe>
		get() = mutableListOf()

	/**
	 * Registers the recipe handler.
	 * @param recipe The recipe to add.
	 * @return True if the recipe was added successfully, false if it already exists.
	 */
	fun addRecipe(recipe: IRecipe): Boolean {
		if (recipes.any { it.name == recipe.name }) {
			return false // Recipe with the same name already exists
		}
		recipes.add(recipe)
		return true
	}

	/**
	 * Unregisters the recipe handler.
	 * @param recipe The recipe to remove.
	 * @return True if the recipe was removed successfully, false if it does not exist.
	 */
	fun removeRecipe(recipe: IRecipe): Boolean {
		return recipes.remove(recipe)
	}

	/**
	 * Returns the recipe that matches the given name.
	 * @param name The name of the recipe to find.
	 * @return The recipe with the given name, or null if no such recipe exists.
	 */
	fun getRecipeByName(name: String): IRecipe? {
		return recipes.firstOrNull { it.name == name }
	}

	/**
	 * Returns the recipe that matches the given output item.
	 * This does not check for exact matches, but rather checks if the output is part of the recipe's outputs.
	 * @param output The output item to find.
	 * @return A list of recipes that produce the given output item.
	 */
	fun getRecipesByOutput(output: ItemStack): List<IRecipe> {
		return recipes.filter { recipe ->
			recipe.getOutputs().any { it.isItemEqual(output) && ItemStack.areItemStackTagsEqual(it, output) }
		}
	}

	/**
	 * Returns the recipe that matches the given input ingredient.
	 * This does not check for exact matches, but rather checks if the input is part of the recipe's inputs.
	 * @param input The input ingredient to find.
	 * @return A list of recipes that can be crafted with the given input ingredient.
	 */
	fun getRecipesByInput(input: Ingredient): List<IRecipe> {
		return recipes.filter { recipe ->
			recipe.getInputs().any { it == input }
		}
	}

	/**
	 * Returns the recipe that matches the given inputs.
	 * This checks if the recipe can be crafted with the given inputs.
	 * @param inputs The list of input ingredients to find.
	 * @return The recipe that matches the given inputs, or null if no such recipe exists.
	 */
	fun getRecipe(inputs: List<Ingredient>): IRecipe? {
		return recipes.firstOrNull { it.matches(inputs) }
	}
}
