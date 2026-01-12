package org.ender_development.catalyx_.modules.integration.groovyscript

import com.cleanroommc.groovyscript.helper.Alias
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import com.google.common.base.CaseFormat
import org.ender_development.catalyx_.core.recipes.Recipe
import org.ender_development.catalyx_.core.recipes.RecipeMap

class VirtualizedRecipeMap(val recipeMap: RecipeMap<*>) : VirtualizedRegistry<Recipe>(Alias.generateOf(recipeMap.unlocalizedName, CaseFormat.LOWER_UNDERSCORE)) {
	override fun onReload() {
		//removeScripted().forEach(recipeMap::removeRecipe)
		//restoreFromBackup().forEach(recipeMap::compileRecipe)
	}
}
