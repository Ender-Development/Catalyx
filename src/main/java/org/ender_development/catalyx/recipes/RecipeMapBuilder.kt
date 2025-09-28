package org.ender_development.catalyx.recipes

import net.minecraft.util.SoundEvent
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.CatalyxSettings

/**
 * A builder for creating a [RecipeMap]. Call [build] on it to return a RecipeMap instance.
 *
 * @param settings the mod settings instance
 * @param unlocalizedName the name of the recipemap
 * @param defaultRecipeBuilder the default recipe builder of the recipemap
 */
class RecipeMapBuilder<B : RecipeBuilder<B>>(
	val settings: CatalyxSettings,
	val unlocalizedName: String,
	val defaultRecipeBuilder: B
) {
	private var itemInputs = 0
	private var itemOutputs = 0
	private var fluidInputs = 0
	private var fluidOutputs = 0

	private var sound: SoundEvent? = null
	private var allowEmptyOutputs = false

	internal constructor(unlocalizedName: String, defaultRecipeBuilder: B) : this(Catalyx.modSettings, unlocalizedName, defaultRecipeBuilder)

	/**
	 * Do not call this twice. RecipeMapBuilders are not re-usable.
	 *
	 * @return a new RecipeMap
	 */
	fun build(): RecipeMap<B> {
		val recipeMap = RecipeMap(settings, unlocalizedName, defaultRecipeBuilder, itemInputs, itemOutputs, fluidInputs, fluidOutputs)
		recipeMap.sound = sound
		recipeMap.allowEmptyOutput = allowEmptyOutputs
		return recipeMap
	}

	/**
	 * @param itemInputs the amount of item inputs
	 * @return this
	 */
	fun itemInputs(count: Int): RecipeMapBuilder<B> {
		require(count >= 0) { Catalyx.LOGGER.error("Item input count must be non-negative, was $count") }
		itemInputs = count
		return this
	}

	/**
	 * @param itemOutputs the amount of item outputs
	 * @return this
	 */
	fun itemOutputs(count: Int): RecipeMapBuilder<B> {
		require(count >= 0) { Catalyx.LOGGER.error("Item output count must be non-negative, was $count") }
		itemOutputs = count
		return this
	}

	/**
	 * @param fluidInputs the amount of fluid inputs
	 * @return this
	 */
	fun fluidInputs(count: Int): RecipeMapBuilder<B> {
		require(count >= 0) { Catalyx.LOGGER.error("Fluid input count must be non-negative, was $count") }
		fluidInputs = count
		return this
	}

	/**
	 * @param fluidOutputs the amount of fluid outputs
	 * @return this
	 */
	fun fluidOutputs(count: Int): RecipeMapBuilder<B> {
		require(count >= 0) { Catalyx.LOGGER.error("Fluid output count must be non-negative, was $count") }
		fluidOutputs = count
		return this
	}

	/**
	 * @param sound the sound to use
	 * @return this
	 */
	fun sound(sound: SoundEvent): RecipeMapBuilder<B> {
		this.sound = sound
		return this
	}

	/**
	 * Make the [RecipeMap] accept recipes without any outputs
	 *
	 * @return this
	 */
	fun allowEmptyOutputs(): RecipeMapBuilder<B> {
		this.allowEmptyOutputs = true
		return this
	}

	/**
	 * Set all available properties
	 *
	 * @param itemInputs the amount of item inputs
	 * @param itemOutputs the amount of item outputs
	 * @param fluidInputs the amount of fluid inputs
	 * @param fluidOutputs the amount of fluid outputs
	 * @param sound the sound to use
	 * @param allowEmptyOutputs whether to allow recipes without any outputs
	 * @return this
	 */
	fun set(itemInputs: Int = this.itemInputs, itemOutputs: Int = this.itemOutputs, fluidInputs: Int = this.fluidInputs, fluidOutputs: Int = this.fluidOutputs, sound: SoundEvent? = this.sound, allowEmptyOutputs: Boolean = this.allowEmptyOutputs): RecipeMapBuilder<B> {
		this.itemInputs = itemInputs
		this.itemOutputs = itemOutputs
		this.fluidInputs = fluidInputs
		this.fluidOutputs = fluidOutputs
		this.sound = sound
		this.allowEmptyOutputs = allowEmptyOutputs
		return this
	}
}
