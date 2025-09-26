package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
import net.minecraft.util.SoundEvent
import org.ender_development.catalyx.CatalyxSettings
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.integration.groovyscript.VirtualizedRecipeMap
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction
import org.ender_development.catalyx.recipes.maps.AbstractMapIngredient
import org.ender_development.catalyx.recipes.maps.Branch
import org.ender_development.catalyx.utils.Delegates
import org.ender_development.catalyx.utils.extensions.toImmutableList
import java.lang.ref.WeakReference
import java.util.*

class RecipeMap<R : RecipeBuilder<R>> {
	companion object {
		internal val RECIPE_MAP_REGISTRY = Object2ReferenceOpenHashMap<String, RecipeMap<*>>()
		internal val RECIPE_DURATION_THEN_ENERGY = Comparator<Recipe>
			.comparingInt(Recipe::duration)
			.thenComparingLong(Recipe::energyPerTick)
			.thenComparingInt { it.hashCode }
		internal val INGREDIENT_ROOT = WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>()
		internal var foundInvalidRecipe = false

		val DEFAULT_CHANCE_FUNCTION = IBoostFunction.TIER

		val recipeMaps: List<RecipeMap<out RecipeBuilder<*>>>
			get() = RECIPE_MAP_REGISTRY.values.toList()

		operator fun get(name: String): RecipeMap<out RecipeBuilder<*>>? =
			RECIPE_MAP_REGISTRY[name]

		fun setInvalidRecipeFound(foundInvalidRecipe: Boolean) {
			RecipeMap.foundInvalidRecipe = RecipeMap.foundInvalidRecipe || foundInvalidRecipe
		}
	}

	private lateinit var recipeMap: RecipeMap<R>
	private val FLUID_INGREDIENT_ROOT = WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>()
	private val RECIPE_BY_CATEGORY = Object2ObjectOpenHashMap<RecipeCategory, List<Recipe>>()

	val unlocalizedName: String
	val translationKey: String

	private val recipeBuilderSample: R
	private val primaryRecipeCategory: RecipeCategory
	private var grsVirtualizedRecipeMap: VirtualizedRecipeMap by Delegates.onlyIfLoaded(Mods.GROOVYSCRIPT)
	private val lockup = Branch()

	var chanceBoostFunction = DEFAULT_CHANCE_FUNCTION

	internal var hasOreDictInputs = false
	internal var hasNBTMatcherInputs = false
	internal var allowEmptyOutput = false
	internal var sound: SoundEvent? = null

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
	internal constructor(settings: CatalyxSettings, unlocalizedName: String, defaultRecipeBuilder: R, maxInputs: Int, maxOutputs: Int, maxFluidInputs: Int, maxFluidOutputs: Int) {
		this.unlocalizedName = unlocalizedName
		this.maxInputs = maxInputs
		this.maxFluidInputs = maxFluidInputs
		this.maxOutputs = maxOutputs
		this.maxFluidOutputs = maxFluidOutputs
		translationKey = "recipemap.${settings.modId}.$unlocalizedName.name"
		primaryRecipeCategory = RecipeCategory.create(settings.modId, unlocalizedName, translationKey, this)

		defaultRecipeBuilder.recipeMap = this
		defaultRecipeBuilder.category = primaryRecipeCategory
		recipeBuilderSample = defaultRecipeBuilder
		RECIPE_MAP_REGISTRY[unlocalizedName] = this

		if(ModuleManager.isModuleEnabled(CatalyxModules.MODULE_GRS))
			grsVirtualizedRecipeMap = VirtualizedRecipeMap(this)
	}
}
