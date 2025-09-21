package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
//import org.ender_development.catalyx.recipes.Recipe

class Branch {
	// Keys on this have *(should)* unique hashcodes.
	private var nodes: Map<AbstractMapIngredient, Either<Recipe, Branch>>? = null
	// Keys on this have collisions, and must be differentiated by equality.
	private var specialNodes: Map<AbstractMapIngredient, Either<Recipe, Branch>>? = null

	fun getRecipes(filterHidden: Boolean): Iterable<Recipe> {
		if(nodes == null && specialNodes == null)
			return emptyList()

		val stream: MutableList<Recipe> = mutableListOf()

		nodes?.let {
			it.values.forEach {
				it.map({ stream.add(it) }, { stream.addAll(it.getRecipes(filterHidden)) })
			}
		}

		specialNodes?.let {
			it.values.forEach {
				it.map({ stream.add(it) }, { stream.addAll(it.getRecipes(filterHidden)) })
			}
		}

		if(filterHidden)
			stream.removeIf { it.hidden }

		return stream
	}

	val empty: Boolean
		get() = nodes?.isEmpty() != false && specialNodes?.isEmpty() != false

	fun getNodes(): Map<AbstractMapIngredient, Either<Recipe, Branch>> =
		nodes ?: Object2ObjectOpenHashMap<AbstractMapIngredient, Either<Recipe, Branch>>().also {
			nodes = it
		}

	fun getSpecialNodes(): Map<AbstractMapIngredient, Either<Recipe, Branch>> =
		specialNodes ?: Object2ObjectOpenHashMap<AbstractMapIngredient, Either<Recipe, Branch>>().also {
			specialNodes = it
		}
}

class Recipe(val hidden: Boolean)
