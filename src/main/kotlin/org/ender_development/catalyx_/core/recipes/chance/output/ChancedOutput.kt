package org.ender_development.catalyx_.core.recipes.chance.output

import org.ender_development.catalyx_.core.recipes.chance.ChancedBase

open class ChancedOutput<T>(ingredient: T, chance: Int) : ChancedBase<T>(ingredient, chance) {
	override fun toString() =
		"ChancedOutput{ingredient=$ingredient, chance=$chance}"
}
