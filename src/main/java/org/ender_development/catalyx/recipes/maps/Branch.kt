package org.ender_development.catalyx.recipes.maps

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.ender_development.catalyx.recipes.Recipe
import java.util.stream.Stream

class Branch {

	// Keys on this have *(should)* unique hashcodes.
	private var nodes: Map<AbstractMapIngredient, Either<Recipe, Branch>>? = null
	// Keys on this have collisions, and must be differentiated by equality.
	private var specialNodes: Map<AbstractMapIngredient, Either<Recipe, Branch>>? = null

	fun getRecipes(filterHidden: Boolean): Stream<Recipe> {
		var stream: Stream<Recipe>? = null

		nodes?.let { it ->
			stream = it.values.stream()
				.flatMap { either -> either.map({ Stream.of(it) }, { right -> right.getRecipes(filterHidden) }) }
		}

		specialNodes?.let { it ->
			stream = if(stream == null) {
				it.values.stream()
					.flatMap { either -> either.map({ Stream.of(it) }, { right -> right.getRecipes(filterHidden) }) }
			} else {
				Stream.concat(
					stream,
					it.values.stream()
						.flatMap { either -> either.map({ Stream.of(it) }, { right -> right.getRecipes(filterHidden) }) }
				)
			}
		}

		return stream?.let {
			if(filterHidden) {
				it.filter { recipe -> !recipe.hidden }
			} else {
				it
			}
		} ?: Stream.empty()
	}

	fun isEmptyBranch(): Boolean =
		(nodes == null || nodes!!.isEmpty()) && (specialNodes == null || specialNodes!!.isEmpty())

	fun getNodes(): Map<AbstractMapIngredient, Either<Recipe, Branch>> {
		if(nodes == null) {
			nodes = Object2ObjectOpenHashMap()
		}
		return nodes!!
	}

	fun getSpecialNodes(): Map<AbstractMapIngredient, Either<Recipe, Branch>> {
		if(specialNodes == null) {
			specialNodes = Object2ObjectOpenHashMap()
		}
		return specialNodes!!
	}
}

