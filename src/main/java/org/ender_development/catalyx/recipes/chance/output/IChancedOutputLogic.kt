package org.ender_development.catalyx.recipes.chance.output

import com.google.common.collect.ImmutableList
import org.ender_development.catalyx.Catalyx.RANDOM
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction

interface IChancedOutputLogic {
	companion object {
		/**
		 * Represents 100.00% chance.
		 * @return The maximum chance value.
		 */
		fun getMaxChance(): Int = 10000

		/**
		 * Gets the chance of an output, applying any boosts if applicable.
		 *
		 * @param output The output to get the chance for.
		 * @param boostFunction The boost function to use.
		 * @param recipeTier The tier of the recipe.
		 * @param machineTier The tier of the machine.
		 * @return The chance of the output, after applying any boosts.
		 */
		fun getChance(output: ChancedOutput<*>, boostFunction: IBoostFunction, recipeTier: Int, machineTier: Int): Int {
			if(output is BoostableChancedOutput<*>) {
				return boostFunction.getBoostedChance(output, recipeTier, machineTier)
			}
			return output.getChance()
		}

		/**
		 * Determines if an output passes its chance check.
		 *
		 * @param chance The chance to check against.
		 * @return True if the output passes the chance check, false otherwise.
		 */
		fun passesChance(chance: Int): Boolean = chance > 0 && chance >= RANDOM.nextInt(getMaxChance())
	}

	/**
	 * An output logic that always returns null, meaning no outputs are produced.
	 */
	object NONE: IChancedOutputLogic {
			override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int): List<T>? = null

			override fun getTranslationKey(): String = "${Reference.MODID}.chance_logic.none"

			override fun toString(): String = "ChancedOutputLogic{type=NONE}"
		}

	/**
	 * An output logic that rolls each entry independently, returning all that pass their chance checks.
	 */
	object OR: IChancedOutputLogic {
			override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int): List<T>? {
				var builder: ImmutableList.Builder<T>? = null
				chancedEntries.forEach {
					if (passesChance(getChance(it, boostFunction, baseTier, machineTier))) {
						if (builder == null) builder = ImmutableList.builder()
						builder!!.add(it)
					}
				}
				return builder?.build()
			}

			override fun getTranslationKey(): String = "${Reference.MODID}.chance_logic.or"

			override fun toString(): String = "ChancedOutputLogic{type=OR}"
		}

	/**
	 * An output logic that requires all entries to pass their chance checks, returning all if they do, or null if any fail.
	 */
	object AND: IChancedOutputLogic {
			override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int): List<T>? {
				chancedEntries.forEach {
					if (!passesChance(getChance(it, boostFunction, baseTier, machineTier))) {
						return null
					}
				}
				return ImmutableList.copyOf(chancedEntries)
			}

			override fun getTranslationKey(): String = "${Reference.MODID}.chance_logic.and"

			override fun toString(): String = "ChancedOutputLogic{type=AND}"
		}

	/**
	 * An output logic that returns the first entry that passes its chance check, or null if none do.
	 */
	object XOR: IChancedOutputLogic {
			override fun <I, T : ChancedOutput<I>> roll(chancedEntries: List<T>, boostFunction: IBoostFunction, baseTier: Int, machineTier: Int): List<T>? {
				chancedEntries.forEach {
					if (passesChance(getChance(it, boostFunction, baseTier, machineTier))) {
						return ImmutableList.of(it)
					}
				}
				return null
			}

			override fun getTranslationKey(): String = "${Reference.MODID}.chance_logic.xor"

			override fun toString(): String = "ChancedOutputLogic{type=XOR}"
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

	fun getTranslationKey(): String
}
