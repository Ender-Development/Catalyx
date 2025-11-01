package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.ender_development.catalyx.recipes.Recipe
import org.ender_development.catalyx.utils.Delegates

class Branch {
	// Keys on this have *(should)* unique hashcodes.
	internal val nodes: Object2ObjectOpenHashMap<AbstractMapIngredient, Either<Recipe, Branch>> by Delegates.lazyProperty { Object2ObjectOpenHashMap() }
	// Keys on this have collisions, and must be differentiated by equality.
	internal val specialNodes: Object2ObjectOpenHashMap<AbstractMapIngredient, Either<Recipe, Branch>> by Delegates.lazyProperty { Object2ObjectOpenHashMap() }

	fun getRecipes(filterHidden: Boolean): Iterable<Recipe> {
		if(nodes.isEmpty() && specialNodes.isEmpty())
			return emptyList()

		val stream: MutableList<Recipe> = mutableListOf()

		nodes.let { node ->
			node.values.forEach { either ->
				either.map({ stream.add(it) }, { stream.addAll(it.getRecipes(filterHidden)) })
			}
		}

		specialNodes.let { node ->
			node.values.forEach { either ->
				either.map({ stream.add(it) }, { stream.addAll(it.getRecipes(filterHidden)) })
			}
		}

		if(filterHidden)
			stream.removeIf { it.hidden }

		return stream
	}

	val empty: Boolean
		get() = nodes.isEmpty() && specialNodes.isEmpty()
}
