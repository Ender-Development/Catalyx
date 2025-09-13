package org.ender_development.catalyx.recipes.chance

/**
 * Represents a chance object that associates an ingredient of type T with a chance value.
 *
 * @param T The type of the ingredient.
 */
interface IChance<T> {
	/**
	 * Gets the ingredient associated with this chance object.
	 *
	 * @return The ingredient of type T.
	 */
	fun getIngredient(): T

	/**
	 * Gets the chance value associated with the ingredient.
	 *
	 * @return The chance value as an integer.
	 */
	fun getChance(): Int

	/**
	 * Creates a copy of this chance object.
	 *
	 * @return A new instance of IChance with the same ingredient and chance value.
	 */
	fun copy(): IChance<T>
}
