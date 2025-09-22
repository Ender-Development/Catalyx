package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.groovyscript.VirtualizedRecipeMap
import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction
import org.ender_development.catalyx.recipes.maps.AbstractMapIngredient
import org.ender_development.catalyx.recipes.maps.Branch
import java.lang.ref.WeakReference
import java.util.*

class RecipeMap<R: RecipeBuilder<R>> {
	companion object {
		internal val RECIPE_MAP_REGISTRY = Object2ReferenceOpenHashMap<String, RecipeMap<*>>()
		internal val RECIPE_DURATION_THEN_ENERGY = Comparator<Recipe>.comparingInt(Recipe::hashCode)
		internal val INGREDIENT_ROOT = WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>()
		internal var foundInvalidRecipe = false

		val DEFAULT_CHANCE_FUNCTION = IBoostFunction.TIER
	}

	private lateinit var recipeMap: RecipeMap<R>
	private val FLUID_INGREDIENT_ROOT = WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>()
	private val RECIPE_BY_CATEGORY = Object2ObjectOpenHashMap<RecipeCategory, List<Recipe>>()

	val unlocalizedName: String

	private val recipeBuilderSample: R
	private val primaryRecipeCategory: RecipeCategory
	private val grsVirtualizedRecipeMap: Any?
	private val lockup = Branch()

	var chanceBoostFunction = DEFAULT_CHANCE_FUNCTION

	private var hasOreDictInputs = false
	private var hasNBTMatcherInputs = false

	//private var smallRecipeMap: RecipeMap<*>?

	private var maxInputs: Int
	private var maxOutputs: Int
	private var maxFluidInputs: Int
	private var maxFluidOutputs: Int

	/**
	 * Create and register new instance of RecipeMap with specified properties.
	 *
	 * @param unlocalizedName      the unlocalized name for the RecipeMap
	 * @param defaultRecipeBuilder the default RecipeBuilder for the RecipeMap
	 * @param maxInputs            the maximum item inputs
	 * @param maxOutputs           the maximum item outputs
	 * @param maxFluidInputs       the maximum fluid inputs
	 * @param maxFluidOutputs      the maximum fluid outputs
	 */
	constructor(unlocalizedName: String, defaultRecipeBuilder: R, maxInputs: Int, maxOutputs: Int, maxFluidInputs: Int, maxFluidOutputs: Int) {
		this.unlocalizedName = unlocalizedName
		this.maxInputs = maxInputs
		this.maxFluidInputs = maxFluidInputs
		this.maxOutputs = maxOutputs
		this.maxFluidOutputs = maxFluidOutputs
		this.primaryRecipeCategory = RecipeCategory.create(
			Reference.MODID, unlocalizedName, getTranslationKey(),
			this
		)

		defaultRecipeBuilder.recipeMap = this
		defaultRecipeBuilder.category = primaryRecipeCategory
		this.recipeBuilderSample = defaultRecipeBuilder
		RECIPE_MAP_REGISTRY[unlocalizedName] = this

		this.grsVirtualizedRecipeMap = if(Catalyx.GROOVYSCRIPT) VirtualizedRecipeMap(this) else null
	}

	fun getTranslationKey(): String {
		return "recipemap.$unlocalizedName.name"
	}
}
