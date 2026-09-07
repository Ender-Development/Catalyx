package org.ender_development.catalyx.api.v1.common.recipes.components

/**
 * Determines how chance is rolled for inputs and outputs.
 *
 * [PER_STACK] rolls once for the entire stack - either the full amount is consumed/produced or none.
 * [PER_ITEM] rolls individually for each unit in the stack, resulting in a variable consumed/produced amount.
 */
enum class RollMode { PER_STACK, PER_ITEM }
