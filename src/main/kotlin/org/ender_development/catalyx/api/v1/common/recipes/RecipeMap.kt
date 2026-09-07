package org.ender_development.catalyx.api.v1.common.recipes

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.Catalyx.LOGGER
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent
import org.ender_development.catalyx.api.v1.common.recipes.conditions.ConditionSet

/**
 * A mutable, queryable collection of recipes with shared defaults and conditions.
 *
 * Constructed via [RecipeMap.Builder]. Recipes can be added or removed at runtime
 * to support dynamic recipe availability (e.g. technology unlocks, game progression).
 *
 * Recipe uniqueness is enforced per map: no two recipes may share the same inputs
 * and conditions. Duplicate detection runs on every [addRecipe] call.
 *
 * @property key The unique identifier for this map within a [RecipeRegistry].
 * @property defaultTime Fallback processing time in ticks for recipes that do not
 *   specify their own base time. Must be >= 1.
 * @property defaultEnergy Fallback energy cost per tick for recipes that do not
 *   specify their own base energy. May be negative for generator-type maps.
 * @property sharedConditions Optional conditions applied to all recipes in this map,
 *   combined with each recipe's own conditions per its [org.ender_development.catalyx.api.v1.common.recipes.conditions.ConditionMode].
 */
class RecipeMap internal constructor(
	val key: String,
	val defaultTime: Int,
	val defaultEnergy: Long,
	val sharedConditions: ConditionSet? = null
) {
	private val _recipes = mutableListOf<Recipe>()

	/** Returns an immutable snapshot of all currently registered recipes. */
	val recipes: List<Recipe> get() = _recipes.toList()

	/**
	 * Registers a recipe built via [Recipe.Builder].
	 *
	 * Validates uniqueness by canonical string (inputs + conditions). Logs and skips on duplicate.
	 *
	 * @param recipe The recipe to register.
	 * @return True if added successfully, false if validation failed.
	 */
	fun addRecipe(recipe: Recipe): Boolean {
		val duplicate = _recipes.any { it.canonicalString() == recipe.canonicalString() }
		if (duplicate) {
			LOGGER.error("[RecipeMap:$key] Duplicate recipe detected for id: '${recipe.id}'")
			return false
		}
		_recipes.add(recipe)
		LOGGER.info("[RecipeMap:$key] Added recipe with id: '${recipe.id}'")
		return true
	}

	/**
	 * Removes the recipe with the given [id].
	 *
	 * @param id The id of the recipe to remove.
	 * @return True if found and removed, false otherwise.
	 */
	fun removeRecipe(id: String): Boolean {
		val removed = _recipes.removeIf { it.id == id }
		if (!removed) LOGGER.warn("[RecipeMap:$key] No recipe found with id: '$id'")
		else LOGGER.info("[RecipeMap:$key] Removed recipe with id: '$id'")
		return removed
	}

	internal fun findRecipe(inputs: List<RecipeComponent>, world: World?, pos: BlockPos?): Recipe? {
		return _recipes.firstOrNull { recipe ->
			matchesConditions(recipe, world, pos) && matchesInputs(recipe, inputs)
		}.also { recipe ->
			if (recipe == null) LOGGER.debug("[RecipeMap:$key] No matching recipe found")
			else LOGGER.debug("[RecipeMap:$key] Matched recipe with id: '${recipe.id}'")
		}
	}

	internal fun resolveTime(recipe: Recipe): Int = recipe.baseTime ?: defaultTime
	internal fun resolveEnergy(recipe: Recipe): Long = recipe.baseEnergy ?: defaultEnergy

	private fun matchesConditions(recipe: Recipe, world: World?, pos: BlockPos?): Boolean {
		if (world == null) return recipe.conditionSet == null && sharedConditions == null
		val mergedConditions = recipe.conditionSet?.mergeWith(sharedConditions) ?: sharedConditions
		return mergedConditions?.evaluate(world, pos) ?: true
	}

	private fun matchesInputs(recipe: Recipe, provided: List<RecipeComponent>): Boolean {
		return recipe.inputs.all { required -> provided.any { required.matches(it) } }
	}

	/**
	 * Builder for constructing [RecipeMap] instances.
	 *
	 * [key], [time], and [energy] are all required.
	 * All validation errors are collected and logged before returning null on failure.
	 *
	 * Example:
	 * ```kotlin
	 * val map = RecipeMap.Builder()
	 *     .key("pressing")
	 *     .time(200)
	 *     .energy(1000L)
	 *     .add(myRecipe)
	 *     .build()
	 * ```
	 */
	class Builder {
		private var key: String? = null
		private var defaultTime: Int? = null
		private var defaultEnergy: Long? = null
		private var sharedConditions: ConditionSet? = null
		private val recipes = mutableListOf<Recipe>()

		/** Sets the unique key for this map. Required. */
		fun key(key: String) = apply { this.key = key }

		/** Sets the default processing time in ticks. Required. Must be >= 1. */
		fun time(ticks: Int) = apply { this.defaultTime = ticks }

		/** Sets the default energy cost per tick. Required. May be negative. */
		fun energy(energy: Long) = apply { this.defaultEnergy = energy }

		/** Sets shared conditions applied to all recipes in this map. */
		fun conditions(conditions: ConditionSet) = apply { sharedConditions = conditions }

		/** Stages a recipe for registration. Duplicate detection runs during [build]. */
		fun add(recipe: Recipe) = apply { recipes.add(recipe) }

		/**
		 * Validates and builds the [RecipeMap].
		 *
		 * @return The constructed [RecipeMap], or null if validation failed. All errors are logged.
		 */
		fun build(): RecipeMap? {
			val errors = mutableListOf<String>()

			if (key.isNullOrBlank()) errors.add("RecipeMap must have a key")
			if (defaultTime == null) errors.add("RecipeMap must have a defaultTime")
			if (defaultTime != null && defaultTime!! < 1) errors.add("defaultTime must be >= 1 tick")
			if (defaultEnergy == null) errors.add("RecipeMap must have a defaultEnergy")

			if (errors.isNotEmpty()) {
				errors.forEach { LOGGER.error("[RecipeMapBuilder] $it") }
				return null
			}

			val map = RecipeMap(
				key = key!!,
				defaultTime = defaultTime!!,
				defaultEnergy = defaultEnergy!!,
				sharedConditions = sharedConditions
			)

			recipes.forEach { map.addRecipe(it) }
			return map
		}
	}
}


