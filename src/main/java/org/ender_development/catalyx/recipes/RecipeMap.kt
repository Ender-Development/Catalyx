package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.util.SoundEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.integration.groovyscript.VirtualizedRecipeMap
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction
import org.ender_development.catalyx.recipes.maps.AbstractMapIngredient
import org.ender_development.catalyx.recipes.maps.Branch
import org.ender_development.catalyx.recipes.validation.ValidationResult
import org.ender_development.catalyx.recipes.validation.ValidationState
import org.ender_development.catalyx.recipes.validation.Validator
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

	var maxInputs: Int
	var maxOutputs: Int
	var maxFluidInputs: Int
	var maxFluidOutputs: Int

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

	/**
	 * Internal usage only, use [RecipeBuilder.buildAndRegister]!!!
	 *
	 * @param validationResult the validation result from building the recipe
	 * @return if adding the recipe was successful
	 */
	internal fun addRecipe(validationResult: ValidationResult<Recipe>): Boolean {
		val result = postValidateRecipe(validationResult)
		return when(result.type) {
			ValidationState.SKIP ->
				false
			ValidationState.INVALID -> {
				setInvalidRecipeFound(true)
				false
			}
			else -> {
				val recipe = result.result
				if(recipe.groovyRecipe)
					grsVirtualizedRecipeMap.addScripted(recipe)

				compileRecipe(recipe)
			}
		}
	}

	/**
	 * Compiles a recipe and adds it to the ingredient tree
	 *
	 * @param recipe the recipe to compile
	 * @return if the recipe was successfully compiled
	 */
	private fun compileRecipe(recipe: Recipe?): Boolean {
		if(recipe == null)
			return false
		val items = fromRecipe(recipe)
		TODO("I'm out of time, finish this later")
		return false
	}

	private fun fromRecipe(recipe: Recipe): List<List<AbstractMapIngredient>> {
		val list = ObjectArrayList<List<AbstractMapIngredient>>(recipe.inputs.size + recipe.fluidInputs.size)
		TODO("Add the actual logic here")
		return list
	}

	private fun postValidateRecipe(validationResult: ValidationResult<Recipe>): ValidationResult<Recipe> {
		val recipe = validationResult.result
		if(recipe.groovyRecipe)
			return validationResult

		val validator = Validator()
		validator.error(recipe.inputs.isEmpty() && recipe.fluidInputs.isEmpty(), "Invalid amounts of recipe inputs. Recipe inputs are empty.")
		validator.assert(recipe.energyPerTick != 0L, "Energy per tick must not be 0")
		validator.error(
				!allowEmptyOutput
						&& recipe.outputs.isEmpty() && recipe.fluidOutputs.isEmpty()
						&& recipe.chancedOutputs.chancedElements.isEmpty() && recipe.chancedFluidOutputs.chancedElements.isEmpty(),
			"Invalid amounts of recipe outputs. Recipe outputs are empty."
		)
		validator.error(recipe.inputs.size > maxInputs, "Invalid amounts of item inputs. Recipe has ${recipe.inputs.size} item inputs, but the maximum is $maxInputs.")
		validator.error(recipe.outputs.size + recipe.chancedOutputs.chancedElements.size > maxOutputs, "Invalid amounts of item outputs. Recipe has ${recipe.outputs.size + recipe.chancedOutputs.chancedElements.size} item outputs, but the maximum is $maxOutputs.")
		validator.error(recipe.fluidInputs.size > maxFluidInputs, "Invalid amounts of fluid inputs. Recipe has ${recipe.fluidInputs.size} fluid inputs, but the maximum is $maxFluidInputs.")
		validator.error(recipe.fluidOutputs.size + recipe.chancedFluidOutputs.chancedElements.size > maxFluidOutputs, "Invalid amounts of fluid outputs. Recipe has ${recipe.fluidOutputs.size + recipe.chancedFluidOutputs.chancedElements.size} fluid outputs, but the maximum is $maxFluidOutputs.")
		validator.logMessages()
		return ValidationResult(validator.status, recipe)
	}
}
