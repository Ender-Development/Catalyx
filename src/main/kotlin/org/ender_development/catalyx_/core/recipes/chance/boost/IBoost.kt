package org.ender_development.catalyx_.core.recipes.chance.boost

import org.ender_development.catalyx_.core.recipes.chance.IChance

/**
 * Represents a boost object that associates an ingredient of type T with both a chance value and a boost value.
 *
 * @param T The type of the ingredient.
 */
interface IBoost<T> : IChance<T> {
	/**
	 * The boost value associated with the ingredient.
	 */
	val boost: Int
}
