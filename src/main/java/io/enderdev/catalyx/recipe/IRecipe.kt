package io.enderdev.catalyx.recipe

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

interface IRecipe {
	/**
	 * Returns the name of the recipe.
	 * Recipe names should be unique.
	 */
	val name: String

	/**
	 * Returns the output item of the recipe.
	 * @return A list of output items produced by the recipe.
	 */
	fun getOutputs(): List<ItemStack>

	/**
	 * Returns the input items of the recipe.
	 * @return A list of input ingredients required to craft the recipe.
	 */
	fun getInputs(): List<Ingredient>

	/**
	 * Checks if the recipe matches the given inputs.
	 * @param inputs The list of input ingredients to check.
	 * @return True if the recipe can be crafted with the given inputs, false otherwise.
	 */
	fun matches(inputs: List<Ingredient>): Boolean
}
