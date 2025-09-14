package org.ender_development.catalyx.recipes.chance

/**
 * Represents a chance object that associates an ingredient of type T with a chance value.
 *
 * @param T The type of the ingredient.
 */
interface IChance<T> {
	/**
	 * The ingredient associated with this chance object.
	 */
	val ingredient: T

	/**
	 * The chance value associated with the ingredient.
	 */
	val chance: Int

	/**
	 * Creates a copy of this chance object.
	 *
	 * @return A new instance of IChance with the same ingredient and chance value.
	 */
	fun copy(): IChance<T>
}
