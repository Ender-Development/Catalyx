package org.ender_development.catalyx.api.v1.common.recipes

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeResult
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedInput
import org.ender_development.catalyx.api.v1.common.recipes.components.ResolvedOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode
import org.ender_development.catalyx.api.v1.common.recipes.handlers.StackHandler
import org.ender_development.catalyx.api.v1.common.recipes.modifier.ModifierContext
import org.ender_development.catalyx.api.v1.common.recipes.modifier.ModifierTarget
import kotlin.random.Random

/**
 * The primary machine-facing API for interacting with the recipe system.
 *
 * Each machine holds a [RecipeHandler] bound to a [RecipeRegistry] and a [machineKey].
 * All recipe queries and resolution go through this handler using public stack types -
 * no internal recipe system types are ever exposed to the machine.
 *
 * Typical machine processing flow:
 * ```kotlin
 * val result       = handler.getRecipeFromStacks(inputStacks, world) ?: return
 * val time         = handler.getTime(result, modifiers)
 * val energy       = handler.getEnergy(result, modifiers)
 * val inputStacks  = handler.applyRecipeToStacks(result, inputStacks, modifiers, random)
 * val outputStacks = handler.resolvedOutputsToStacks(result, modifiers, random)
 * // sync inputStacks back to input slots
 * // insert outputStacks into output slots
 * // charge energy for time ticks
 * ```
 *
 * @property registry The [RecipeRegistry] this handler queries against.
 * @property machineKey The key used to look up relevant [RecipeMap]s in the registry.
 */
class RecipeHandler(
	private val registry: RecipeRegistry,
	private val machineKey: String
) {
	/**
	 * Finds the first valid recipe matching the given mixed list of stacks.
	 *
	 * Stacks are converted to internal components via registered [StackHandler]s.
	 * Multiple stacks of the same resource type are aggregated into a single total
	 * amount using the [plus] operator before matching - a recipe requiring 150 iron
	 * ingots matches correctly across multiple stacks summing to >= 150.
	 *
	 * @param stacks All stacks currently in the machine's input inventory. May contain
	 *   any mix of stack types covered by registered [StackHandler]s.
	 * @param world The current [World] snapshot, or null if unavailable.
	 *   When null, only condition-free recipes are eligible.
	 * @param pos The current [BlockPos] the querying block is placed at, or null if unavailable.
	 * @return A [RecipeResult] snapshot, or null if no matching recipe was found.
	 */
	fun getRecipeFromStacks(stacks: List<Any>, world: World?, pos: BlockPos?): RecipeResult? {
		val components = stacks.mapNotNull { stack ->
			@Suppress("UNCHECKED_CAST")
			(StackHandler.forStack(stack) as? StackHandler<Any, *, *>)?.stackToInput(stack)
		}

		val aggregated = components
			.groupBy { it::class }
			.map { (_, group) -> group.reduce { acc, component -> acc + component } }

		val map = registry.findMap(machineKey, aggregated, world, pos) ?: return null
		val recipe = map.findRecipe(aggregated, world, pos) ?: return null
		return RecipeResult(recipe, map, world, pos)
	}

	/**
	 * Resolves the effective processing time after applying [modifiers]. Minimum 1 tick.
	 *
	 * @param result The [RecipeResult] from a prior [getRecipeFromStacks] call.
	 * @param modifiers The machine's current [ModifierContext].
	 * @return The effective processing time in ticks.
	 */
	fun getTime(result: RecipeResult, modifiers: ModifierContext): Int {
		val base = result.map.resolveTime(result.recipe).toDouble()
		return (base * modifiers.resolve(ModifierTarget.TIME)).toLong().coerceAtLeast(1L).toInt()
	}

	/**
	 * Resolves the effective energy cost per tick after applying [modifiers].
	 *
	 * @param result The [RecipeResult] from a prior [getRecipeFromStacks] call.
	 * @param modifiers The machine's current [ModifierContext].
	 * @return The effective energy cost per tick. Negative values indicate generation.
	 */
	fun getEnergy(result: RecipeResult, modifiers: ModifierContext): Long {
		val base = result.map.resolveEnergy(result.recipe).toDouble()
		return (base * modifiers.resolve(ModifierTarget.ENERGY)).toLong()
	}

	/**
	 * Combines the effective processing time and energy cost after applying [modifiers].
	 *
	 * @param result The [RecipeResult] from a prior [getRecipeFromStacks] call.
	 * @param modifiers The machine's current [ModifierContext].
	 * @return The absolute energy cost for the given recipe. Negative values indicate generation.
	 */
	fun getAbsolutEnergy(result: RecipeResult, modifiers: ModifierContext): Long =
		getEnergy(result, modifiers) * getTime(result, modifiers)

	/**
	 * Applies consumption to the provided [stacks] and returns the updated list.
	 *
	 * For each input in the recipe, resolves the consume chance and amount after
	 * applying [modifiers] and rolling via [random], then delegates to the appropriate
	 * [StackHandler] to remove the consumed amount from the stack list.
	 *
	 * @param result The [RecipeResult] from a prior [getRecipeFromStacks] call.
	 * @param stacks The current stacks in the machine's input inventory.
	 * @param modifiers The machine's current [ModifierContext].
	 * @param random The shared [Random] instance for chance rolls.
	 * @return The updated stack list after consumption. Ready to sync back to input slots.
	 */
	fun applyRecipeToStacks(
		result: RecipeResult,
		stacks: List<Any>,
		modifiers: ModifierContext,
		random: Random
	): List<Any> {
		val updatedStacks = stacks.toMutableList()
		getConsume(result, modifiers, random).forEach { resolved ->
			@Suppress("UNCHECKED_CAST")
			(StackHandler.forComponent(resolved.component) as? StackHandler<Any, *, *>)
				?.consumeFromStacks(updatedStacks, resolved)
		}
		return updatedStacks.toList()
	}

	/**
	 * Resolves the outputs and returns them as a list of public stacks ready for output slots.
	 *
	 * For each output in the recipe, resolves the produce chance and amount after applying
	 * [modifiers] and rolling via [random], then delegates to the appropriate [StackHandler]
	 * to convert the result back to a public stack. Outputs of the same resource type are
	 * combined into a single stack before conversion.
	 *
	 * Resource types without a registered handler (e.g. pure energy outputs) are silently
	 * skipped - the machine is responsible for handling those separately.
	 *
	 * Note: each call re-rolls all output chances via [random]. Do not call multiple times
	 * for the same process - store and reuse the result.
	 *
	 * @param result The [RecipeResult] from a prior [getRecipeFromStacks] call.
	 * @param modifiers The machine's current [ModifierContext].
	 * @param random The shared [Random] instance for chance rolls.
	 * @return A list of output stacks. May be empty if all chances failed or the recipe
	 *   has no outputs handled by a registered [StackHandler].
	 */
	fun resolvedOutputsToStacks(
		result: RecipeResult,
		modifiers: ModifierContext,
		random: Random
	): List<Any> {
		return getOutput(result, modifiers, random).mapNotNull { resolved ->
			StackHandler.forComponent(resolved.component)?.resolvedToStack(resolved)
		}
	}

	private fun getConsume(
		result: RecipeResult,
		modifiers: ModifierContext,
		random: Random
	): List<ResolvedInput> {
		val chanceModifier = modifiers.resolve(ModifierTarget.CONSUME_CHANCE)
		return result.recipe.inputs.map { input ->
			val finalChance = (input.consumeChance * chanceModifier).coerceIn(0.0, 1.0)
			val consumedAmount = when {
				input.consumeChance == 0.0 -> 0
				else -> when (input.rollMode) {
					RollMode.PER_STACK -> if (random.nextDouble() <= finalChance) input.amount else 0
					RollMode.PER_ITEM  -> (0 until input.amount).count { random.nextDouble() <= finalChance }
				}
			}
			ResolvedInput(input, consumedAmount)
		}
	}

	private fun getOutput(
		result: RecipeResult,
		modifiers: ModifierContext,
		random: Random
	): List<ResolvedOutput> {
		val amountModifier = modifiers.resolve(ModifierTarget.OUTPUT_AMOUNT)
		val chanceModifier = modifiers.resolve(ModifierTarget.OUTPUT_CHANCE)

		return result.recipe.outputs
			.map { output ->
				val finalChance = (output.produceChance * chanceModifier).coerceIn(0.01, 1.0)
				val finalAmount = (output.amount * amountModifier).toInt().coerceAtLeast(0)
				val producedAmount = when (output.rollMode) {
					RollMode.PER_STACK -> if (random.nextDouble() <= finalChance) finalAmount else 0
					RollMode.PER_ITEM  -> (0 until finalAmount).count { random.nextDouble() <= finalChance }
				}
				ResolvedOutput(output, producedAmount)
			}
			.groupBy { it.component::class }
			.map { (_, group) ->
				group.reduce { acc, resolved ->
					ResolvedOutput(acc.component + resolved.component, acc.amount + resolved.amount)
				}
			}
			.filter { it.amount > 0 }
	}
}
