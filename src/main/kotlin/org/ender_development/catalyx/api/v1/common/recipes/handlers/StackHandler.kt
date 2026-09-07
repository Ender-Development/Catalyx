package org.ender_development.catalyx.api.v1.common.recipes.handlers

import org.ender_development.catalyx.api.v1.common.recipes.components.*

/**
 * Abstract base for type-specific stack handlers.
 *
 * Bridges the gap between public stack types (e.g. [net.minecraft.item.ItemStack], [net.minecraftforge.fluids.FluidStack]) and the
 * recipe system's internal [RecipeComponent]s. Each resource type that can appear in a
 * recipe should have a corresponding [StackHandler] declared as an `object`.
 *
 * Handlers self-register on instantiation via the companion object registry - declaring
 * a handler as an `object` and referencing it anywhere in your code is sufficient to
 * make it available to both [org.ender_development.catalyx.api.v1.common.recipes.Recipe.Builder] and [org.ender_development.catalyx.api.v1.common.recipes.RecipeHandler] automatically.
 *
 * Example:
 * ```kotlin
 * object MyStackHandler : StackHandler<MyStack, MyRecipeInput, MyRecipeOutput>() {
 *     override fun canHandle(stack: Any) = stack is MyStack
 *     override fun handles(component: RecipeComponent) =
 *         component is MyRecipeInput || component is MyRecipeOutput
 *     // ... implement remaining abstract methods
 * }
 * ```
 *
 * @param S The public stack type (e.g. [net.minecraft.item.ItemStack], [net.minecraftforge.fluids.FluidStack]).
 * @param I The internal [RecipeInput] subtype this handler produces.
 * @param O The internal [RecipeOutput] subtype this handler produces.
 */
abstract class StackHandler<S : Any, I : RecipeInput, O : RecipeOutput> {

	init {
		registry.add(this)
	}

	companion object {
		private val registry = mutableListOf<StackHandler<*, *, *>>()

		/**
		 * Returns the first registered [StackHandler] that can handle the given public [stack],
		 * or null if none is found.
		 */
		fun forStack(stack: Any): StackHandler<*, *, *>? =
			registry.firstOrNull { it.canHandle(stack) }

		/**
		 * Returns the first registered [StackHandler] that can handle the given internal
		 * [component], or null if none is found.
		 */
		internal fun forComponent(component: RecipeComponent): StackHandler<*, *, *>? =
			registry.firstOrNull { it.handles(component) }

		/**
		 * Returns all currently registered [StackHandler]s.
		 */
		fun all(): List<StackHandler<*, *, *>> = registry.toList()
	}

	/**
	 * Returns true if this handler can process the given public [stack].
	 *
	 * @param stack The public stack to check.
	 */
	abstract fun canHandle(stack: Any): Boolean

	/**
	 * Returns true if this handler can process the given internal [component].
	 *
	 * Should return true for both the input and output types this handler manages:
	 * ```kotlin
	 * override fun handles(component: RecipeComponent) =
	 *     component is MyRecipeInput || component is MyRecipeOutput
	 * ```
	 *
	 * @param component The internal component to check.
	 */
	internal abstract fun handles(component: RecipeComponent): Boolean

	/**
	 * Converts a single public stack into an internal [RecipeInput].
	 * The required amount is read directly from the stack.
	 *
	 * @param stack The stack to convert.
	 * @param consumeChance The consume chance. Defaults to 1.0.
	 * @param rollMode The roll mode. Defaults to [RollMode.PER_STACK].
	 */
	internal abstract fun stackToInput(
		stack: S,
		consumeChance: Double = 1.0,
		rollMode: RollMode = RollMode.PER_STACK
	): I

	/**
	 * Converts a list of equivalent stacks into a single internal [RecipeInput] that
	 * accepts any of the provided stacks as a valid resource.
	 * The required amount is read from the first stack.
	 *
	 * @param stacks The list of equivalent stacks. Must not be empty.
	 * @param consumeChance The consume chance. Defaults to 1.0.
	 * @param rollMode The roll mode. Defaults to [RollMode.PER_STACK].
	 */
	internal abstract fun stackGroupToInput(
		stacks: List<S>,
		consumeChance: Double = 1.0,
		rollMode: RollMode = RollMode.PER_STACK
	): I

	/**
	 * Converts a public stack into an internal [RecipeOutput].
	 * Amount is passed explicitly since output amounts are defined by the recipe, not the stack.
	 *
	 * @param stack The stack identifying the resource type.
	 * @param produceChance The produce chance. Defaults to 1.0.
	 * @param rollMode The roll mode. Defaults to [RollMode.PER_STACK].
	 */
	internal abstract fun stackToOutput(
		stack: S,
		produceChance: Double = 1.0,
		rollMode: RollMode = RollMode.PER_STACK
	): O

	/**
	 * Applies the consumption described by [resolved] to the given mutable [stacks] list.
	 * Removes amounts from matching stacks in list order, removing fully consumed stacks.
	 *
	 * @param stacks The mutable list of stacks to consume from.
	 * @param resolved The resolved input describing what and how much to consume.
	 */
	internal abstract fun consumeFromStacks(stacks: MutableList<S>, resolved: ResolvedInput)

	/**
	 * Converts an internal [ResolvedOutput] into a public stack of type [S],
	 * or null if this handler cannot handle the output type or the amount is 0.
	 *
	 * @param resolved The resolved output to convert.
	 */
	internal abstract fun resolvedToStack(resolved: ResolvedOutput): S?
}


