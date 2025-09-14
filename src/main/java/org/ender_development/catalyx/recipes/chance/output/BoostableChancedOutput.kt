package org.ender_development.catalyx.recipes.chance.output

import org.ender_development.catalyx.recipes.chance.boost.IBoost

open class BoostableChancedOutput<T>(ingredient: T, chance: Int, override val boost: Int) : ChancedOutput<T>(ingredient, chance), IBoost<T> {
	override fun toString() =
		"BoostableChancedOutput{ingredient=$ingredient, chance=$chance, boost=$boost}"
}
