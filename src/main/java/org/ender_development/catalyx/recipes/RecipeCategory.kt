package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*

class RecipeCategory {
	private val modid: String
	private val name: String
	private val uniqueID: String
	private val translationKey: String
	private val recipeMap: RecipeMap<*>
	private lateinit var icon: Any

	private constructor(modid: String, name: String, translationKey: String, recipeMap: RecipeMap<*>) {
		this.modid = modid
		this.name = name
		this.uniqueID = "${this.modid}.${this.name}"
		this.translationKey = translationKey
		this.recipeMap = recipeMap
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
			categories.computeIfAbsent(categoryName) { _: Any? -> RecipeCategory(modid, categoryName, translationKey, recipeMap) }

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

	/**
	 * The icon can be an [net.minecraft.item.ItemStack] or any other format supported by JEI.
	 *
	 * @param icon the icon to use as a JEI category
	 * @return this
	 */
	fun jeiIcon(icon: Any?): RecipeCategory {
		this.icon = icon!!
		return this
	}

	override fun equals(other: Any?): Boolean {
		if(this === other) return true
		if(other == null || javaClass != other.javaClass) return false

		val that = other as RecipeCategory
		return uniqueID == that.uniqueID
	}

	override fun hashCode(): Int =
		uniqueID.hashCode()

	override fun toString(): String =
		"RecipeCategory{$uniqueID}"
}
