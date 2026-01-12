package org.ender_development.catalyx.core.recipes.chance.output

import org.ender_development.catalyx.Catalyx.RANDOM
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.recipes.chance.boost.IBoostFunction

interface IChancedOutputLogic {
	companion object {
		/**
		 * Represents 100.00% chance.
		 */
		const val MAX_CHANCE = 10000

		/**
		 * Gets the chance of an output, applying any boosts if applicable.
		 *
		 * @param output The output to get the chance for.
		 * @param boostFunction The boost function to use.
		 * @param recipeTier The tier of the recipe.
		 * @param machineTier The tier of the machine.
		 * @return The chance of the output, after applying any boosts.
		 */
		fun getChance(output: ChancedOutput<*>, boostFunction: IBoostFunction, recipeTier: Int, machineTier: Int) =
			if(output is BoostableChancedOutput<*>)
				boostFunction.getBoostedChance(output, recipeTier, machineTier)
			else
				output.chance

		/**
		 * Determines if an output passes its chance check.
		 *
		 * @param chance The chance to check against.
		 * @return True if the output passes the chance check, false otherwise.
		 */
		fun passesChance(chance: Int) =
			chance > 0 && chance >= RANDOM.nextInt(MAX_CHANCE)
	}

	/**
	 * An output logic that always returns null, meaning no outputs are produced.
	 */
	object NONE : IChancedOutputLogic {
		override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int) =
			null

		override val translationKey = "${Reference.MODID}.chance_logic.none"

		override fun toString() =
			"ChancedOutputLogic{type=NONE}"
	}

	/**
	 * An output logic that rolls each entry independently, returning all that pass their chance checks.
	 */
	object OR : IChancedOutputLogic {
		override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int) =
			chancedEntries.filter {
				passesChance(getChance(it, boostFunction, baseTier, machineTier))
			}

		override val translationKey = "${Reference.MODID}.chance_logic.or"

		override fun toString() =
			"ChancedOutputLogic{type=OR}"
	}

	/**
	 * An output logic that requires all entries to pass their chance checks, returning all if they do, or null if any fail.
	 */
	object AND : IChancedOutputLogic {
		override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int) =
			if(chancedEntries.all {
					passesChance(getChance(it, boostFunction, baseTier, machineTier))
				})
				chancedEntries
			else
				null

		override val translationKey = "${Reference.MODID}.chance_logic.and"

		override fun toString() =
			"ChancedOutputLogic{type=AND}"
	}

	/**
	 * An output logic that returns the first entry that passes its chance check, or null if none do.
	 */
	object FIRST : IChancedOutputLogic {
		override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int) =
			chancedEntries.firstNotNullOfOrNull {
				if(passesChance(getChance(it, boostFunction, baseTier, machineTier)))
					listOf(it)
				else
					null
			}

		override val translationKey = "${Reference.MODID}.chance_logic.first"

		override fun toString() =
			"ChancedOutputLogic{type=FIRST}"
	}

	/**
	 * Rolls the given chanced entries using the specified boost function and tiers.
	 * @param chancedEntries The list of chanced entries to roll.
	 * @param boostFunction The boost function to apply to the chances.
	 * @param baseTier The base tier of the recipe.
	 * @param machineTier The tier of the machine processing the recipe.
	 * @return A list of chanced entries that passed their chance checks, or null if none did.
	 */
	fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int): List<T>?

	val translationKey: String
}
