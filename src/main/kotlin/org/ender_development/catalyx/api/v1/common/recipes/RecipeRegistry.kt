package org.ender_development.catalyx.api.v1.common.recipes

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.Catalyx.LOGGER
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeComponent

/**
 * The central registry mapping machine keys to their associated [RecipeMap]s.
 *
 * Machines interact with the recipe system by holding a [RecipeHandler] bound to this
 * registry and their machine key. The registry is mutable - maps can be added or removed
 * at runtime to support dynamic recipe availability.
 *
 * A single machine key may be associated with multiple [RecipeMap]s, searched in
 * registration order - the first matching recipe wins.
 */
class RecipeRegistry {
	private val entries = mutableMapOf<String, MutableList<RecipeMap>>()

	/**
	 * Registers a [RecipeMap] under the given machine [key].
	 * Multiple maps can be registered under the same key and are searched in registration order.
	 *
	 * @param key The machine key to associate this map with.
	 * @param map The [RecipeMap] to register.
	 */
	fun addMap(key: String, map: RecipeMap) {
		entries.getOrPut(key) { mutableListOf() }.add(map)
		LOGGER.info("[RecipeRegistry] Added RecipeMap '${map.key}' under key: '$key'")
	}

	/**
	 * Removes the [RecipeMap] with the given [mapKey] from under the given machine [key].
	 *
	 * @param key The machine key the map is registered under.
	 * @param mapKey The key of the map to remove.
	 * @return True if found and removed, false otherwise.
	 */
	fun removeMap(key: String, mapKey: String): Boolean {
		val removed = entries[key]?.removeIf { it.key == mapKey } ?: false
		if (!removed) LOGGER.warn("[RecipeRegistry] No RecipeMap '$mapKey' under key: '$key'")
		else LOGGER.info("[RecipeRegistry] Removed RecipeMap '$mapKey' under key: '$key'")
		return removed
	}

	/**
	 * Returns all [RecipeMap]s registered under the given machine [key].
	 *
	 * @param key The machine key to look up.
	 * @return An immutable list of registered maps, or an empty list if none found.
	 */
	fun getMaps(key: String): List<RecipeMap> {
		return entries[key]?.toList() ?: emptyList<RecipeMap>().also {
			LOGGER.warn("[RecipeRegistry] No RecipeMaps found under key: '$key'")
		}
	}

	internal fun findMap(
		key: String,
		inputs: List<RecipeComponent>,
		world: World?,
		pos: BlockPos?
	): RecipeMap? {
		return getMaps(key).firstOrNull { map ->
			map.findRecipe(inputs, world, pos) != null
		}.also { map ->
			if (map == null) LOGGER.debug("[RecipeRegistry] No matching RecipeMap under key: '$key'")
			else LOGGER.debug("[RecipeRegistry] Found RecipeMap '${map.key}' under key: '$key'")
		}
	}

	/**
	 * Returns all registered maps across all machine keys.
	 * Useful for debugging and tooling.
	 *
	 * @return An immutable map of machine key to list of [RecipeMap]s.
	 */
	fun getAllMaps(): Map<String, List<RecipeMap>> = entries.mapValues { it.value.toList() }

	/**
	 * Returns the number of [RecipeMap]s registered under the given machine [key].
	 *
	 * @param key The machine key to count maps for.
	 * @return The number of registered maps, or 0 if the key is not found.
	 */
	fun getMapCount(key: String): Int = entries[key]?.size ?: 0
}
