package org.ender_development.catalyx_.core.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

class RecipeCategory {
	private val modid: String
	private val name: String
	private val uniqueID: String
	private val translationKey: String
	val recipeMap: RecipeMap<*>
	/**
	 * The icon can be an [net.minecraft.item.ItemStack] or any other format supported by JEI.
	 */
	lateinit var jeiIcon: Any

	private constructor(modid: String, name: String, translationKey: String, recipeMap: RecipeMap<*>) {
		this.modid = modid
		this.name = name
		this.translationKey = translationKey
		this.recipeMap = recipeMap
		uniqueID = "$modid.$name"
	}

	companion object {
		private val categories = Object2ObjectOpenHashMap<String, RecipeCategory>()

		/**
		 * Create a RecipeCategory
		 *
		 * @param modid          the mod id of the category
		 * @param categoryName   the name of the category
		 * @param translationKey the translation key of the category.
		 * @param recipeMap      the recipemap that accepts this category
		 * @return the new category
		 */
		fun create(modid: String, categoryName: String, translationKey: String, recipeMap: RecipeMap<*>): RecipeCategory =
			categories.computeIfAbsent(categoryName) { RecipeCategory(modid, categoryName, translationKey, recipeMap) }

		/**
		 * @param categoryName the name of the category
		 * @return the category associated with the name
		 */
		fun getByName(categoryName: String): RecipeCategory? =
			categories[categoryName]

		/**
		 * @return all the RecipeCategory instances
		 */
		fun getCategories(): Collection<RecipeCategory> =
			Collections.unmodifiableCollection(categories.values)
	}

	override fun equals(other: Any?) =
		this === other || (other is RecipeCategory && uniqueID == other.uniqueID)

	override fun hashCode() =
		uniqueID.hashCode()

	override fun toString(): String =
		"RecipeCategory{$uniqueID}"
}
