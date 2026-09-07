package org.ender_development.catalyx.api.v1.common.recipes.conditions

/**
 * Determines how a [org.ender_development.catalyx.api.v1.common.recipes.Recipe]'s [ConditionSet] is combined with its parent [org.ender_development.catalyx.api.v1.common.recipes.RecipeMap]'s shared conditions.
 *
 * [AND] requires both the map's and the recipe's conditions to be satisfied. This is the default.
 * [OR] requires at least one of the map's or the recipe's conditions to be satisfied.
 * [XOR] requires an odd number of conditions across both sets to be satisfied.
 * [REPLACE] ignores the map's conditions entirely, only evaluating the recipe's own conditions.
 */
enum class ConditionMode { AND, OR, XOR, REPLACE }


