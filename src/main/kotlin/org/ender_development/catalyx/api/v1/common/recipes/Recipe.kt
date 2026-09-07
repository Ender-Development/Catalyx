package org.ender_development.catalyx.api.v1.common.recipes

import net.minecraft.item.crafting.Ingredient
import org.ender_development.catalyx.Catalyx.LOGGER
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeInput
import org.ender_development.catalyx.api.v1.common.recipes.components.RecipeOutput
import org.ender_development.catalyx.api.v1.common.recipes.components.RollMode
import org.ender_development.catalyx.api.v1.common.recipes.conditions.ConditionSet
import org.ender_development.catalyx.api.v1.common.recipes.handlers.StackHandler
import java.security.MessageDigest

/**
 * Internal immutable definition of a single recipe.
 *
 * End users never construct or reference [Recipe] directly - use [Recipe.Builder]
 * to define recipes via public stack types and pass them to [RecipeMap.Builder].
 */
class Recipe private constructor(
	val id: String,
	val inputs: List<RecipeInput>,
	val outputs: List<RecipeOutput>,
	val conditionSet: ConditionSet?,
	val baseTime: Int?,
	val baseEnergy: Long?
) {
	fun canonicalString(): String {
		val inputPart = inputs.map { it.toString() }.sorted().joinToString("|")
		val conditionPart = conditionSet?.toString() ?: "NO_CONDITIONS"
		return "$inputPart||$conditionPart"
	}

	companion object {
		fun deriveId(canonicalString: String): String {
			val digest = MessageDigest.getInstance("SHA-256")
			val hashBytes = digest.digest(canonicalString.toByteArray(Charsets.UTF_8))
			return hashBytes.joinToString("") { "%02x".format(it) }
		}
	}

	/**
	 * Builder for constructing recipes using public stack types.
	 *
	 * All inputs and outputs are defined via stacks - the builder delegates to registered
	 * [StackHandler]s automatically. No knowledge of internal types is required.
	 *
	 * Example:
	 * ```kotlin
	 * val recipe = Recipe.Builder()
	 *     .addInput(ItemStack(Items.IRON_INGOT, 2))
	 *     .addInput(
	 *         listOf(
	 *             ItemStack(Items.GOLD_INGOT, 1),
	 *             ItemStack(Items.DIAMOND, 1)
	 *         )
	 *     )
	 *     .addOutput(ItemStack(Items.STICK), amount = 4)
	 *     .time(100)
	 *     .power(500L)
	 *     .build()
	 * ```
	 */
	class Builder {
		private var id: String? = null
		private val inputs = mutableListOf<RecipeInput>()
		private val outputs = mutableListOf<RecipeOutput>()
		private var conditionSet: ConditionSet? = null
		private var baseTime: Int? = null
		private var baseEnergy: Long? = null

		/** Sets an explicit id. If omitted, a stable SHA-256 hash of inputs and conditions is used. */
		fun id(id: String) = apply { this.id = id }

		/** Sets the condition set for this recipe. */
		fun conditions(conditionSet: ConditionSet) = apply { this.conditionSet = conditionSet }

		/** Overrides the parent [RecipeMap]'s default processing time. Must be >= 1 tick. */
		fun time(ticks: Int) = apply { this.baseTime = ticks }

		/** Overrides the parent [RecipeMap]'s default energy cost per tick. May be negative. */
		fun energy(energy: Long) = apply { this.baseEnergy = energy }

		/**
		 * Adds a required input for the given [stack].
		 * The required amount is read directly from the stack.
		 *
		 * @param stack The stack identifying the required resource and amount.
		 * @param consumeChance The probability this input is consumed. Defaults to 1.0.
		 * @param rollMode How consume chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addInput(
			stack: Any,
			consumeChance: Double = 1.0,
			rollMode: RollMode = RollMode.PER_STACK
		) = apply {
			val handler = StackHandler.forStack(stack)
			if (handler == null) {
				LOGGER.error("[RecipeBuilder] Unable to add InputStack. No StackHandler found for: ${stack::class.simpleName}")
				return@apply
			}
			@Suppress("UNCHECKED_CAST")
			inputs.add((handler as StackHandler<Any, *, *>).stackToInput(stack, consumeChance, rollMode))
		}

		/**
		 * Adds a group of equivalent stacks as a single required input.
		 * Any stack in the group satisfies this input requirement.
		 * The required amount is read from the largest stack.
		 * should carry the same amount as they represent alternatives for the same slot.
		 *
		 * @param stacks The list of equivalent stacks. Must not be empty.
		 * @param consumeChance The probability this input is consumed. Defaults to 1.0.
		 * @param rollMode How consume chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addInput(
			stacks: List<Any>,
			consumeChance: Double = 1.0,
			rollMode: RollMode = RollMode.PER_STACK
		) = apply {
			if (stacks.isEmpty()) {
				LOGGER.error("[RecipeBuilder] addInputStackGroup called with empty list")
				return@apply
			}
			val handler = StackHandler.forStack(stacks.first())
			if (handler == null) {
				LOGGER.error("[RecipeBuilder] Unable to add InputStackGroup. No StackHandler found for: ${stacks.first()::class.simpleName}")
				return@apply
			}
			@Suppress("UNCHECKED_CAST")
			inputs.add((handler as StackHandler<Any, *, *>).stackGroupToInput(stacks, consumeChance, rollMode))
		}

		/**
		 * Adds an [Ingredient] as a single required input.
		 * Any stack in it satisfies this input requirement.
		 * The required amount is read from the largest stack.
		 * should carry the same amount as they represent alternatives for the same slot.
		 *
		 * @param stacks The [Ingredient]. Must not be empty.
		 * @param consumeChance The probability this input is consumed. Defaults to 1.0.
		 * @param rollMode How consume chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addInput(
			stacks: Ingredient,
			consumeChance: Double = 1.0,
			rollMode: RollMode = RollMode.PER_STACK
		) = addInput(stacks.matchingStacks.toList(), consumeChance, rollMode)

		/**
		 * Adds a catalyst input for the given [stack].
		 * The stack must be present but is never consumed.
		 * The required amount is read directly from the stack.
		 *
		 * @param stack The stack identifying the catalyst resource and amount.
		 * @param rollMode How the (zero) chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addCatalyst(
			stack: Any,
			rollMode: RollMode = RollMode.PER_STACK
		) = apply { addInput(stack, consumeChance = 0.0, rollMode = rollMode) }

		/**
		 * Adds a group of equivalent catalyst stacks as a single required input.
		 * Any stack in the group satisfies the requirement and none are ever consumed.
		 * The required amount is read from the first stack.
		 *
		 * @param stacks The list of equivalent catalyst stacks. Must not be empty.
		 * @param rollMode How the (zero) chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addCatalyst(
			stacks: List<Any>,
			rollMode: RollMode = RollMode.PER_STACK
		) = apply { addInput(stacks, consumeChance = 0.0, rollMode = rollMode) }

		/**
		 * Adds an output for the given [stack].
		 * Amount is specified explicitly since output amounts are defined by the recipe.
		 *
		 * @param stack The stack identifying the produced resource type.
		 * @param amount The base amount to produce. Defaults to 1.
		 * @param produceChance The probability this output is produced. Defaults to 1.0.
		 * @param rollMode How produce chance is rolled. Defaults to [RollMode.PER_STACK].
		 */
		fun addOutputStack(
			stack: Any,
			amount: Long = 1L,
			produceChance: Double = 1.0,
			rollMode: RollMode = RollMode.PER_STACK
		) = apply {
			val handler = StackHandler.forStack(stack)
			if (handler == null) {
				LOGGER.error("[RecipeBuilder] Unable to add OutputStack. No StackHandler found for: ${stack::class.simpleName}")
				return@apply
			}
			@Suppress("UNCHECKED_CAST")
			outputs.add((handler as StackHandler<Any, *, *>).stackToOutput(stack, produceChance, rollMode))
		}

		/**
		 * Validates and builds the [Recipe].
		 *
		 * @return The constructed [Recipe], or null if validation failed. All errors are logged.
		 */
		fun build(): Recipe? {
			val errors = mutableListOf<String>()

			if (inputs.isEmpty()) errors.add("Recipe must have at least one input")
			if (baseTime != null && baseTime!! < 1) errors.add("baseTime must be >= 1 tick")

			if (errors.isNotEmpty()) {
				errors.forEach { LOGGER.error("[RecipeBuilder] Recipe failed: $it") }
				return null
			}

			val canonical = buildCanonicalString()
			val resolvedId = id ?: Recipe.deriveId(canonical)

			return Recipe(
				id = resolvedId,
				inputs = inputs.toList(),
				outputs = outputs.toList(),
				conditionSet = conditionSet,
				baseTime = baseTime,
				baseEnergy = baseEnergy
			)
		}

		private fun buildCanonicalString(): String {
			val inputPart = inputs.map { it.toString() }.sorted().joinToString("|")
			val conditionPart = conditionSet?.toString() ?: "NO_CONDITIONS"
			return "$inputPart||$conditionPart"
		}
	}
}


