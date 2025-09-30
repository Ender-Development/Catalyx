package org.ender_development.catalyx.recipes

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.util.SoundEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.integration.groovyscript.VirtualizedRecipeMap
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.recipes.chance.boost.IBoostFunction
import org.ender_development.catalyx.recipes.ingredients.RecipeInput
import org.ender_development.catalyx.recipes.maps.*
import org.ender_development.catalyx.recipes.validation.ValidationResult
import org.ender_development.catalyx.recipes.validation.ValidationState
import org.ender_development.catalyx.recipes.validation.Validator
import org.ender_development.catalyx.utils.Delegates
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

		/**
		 * Builds a list of unique inputs from the given list [RecipeInput]s.
		 * Used to reduce the number inputs, if for example there is more than one of the same input, pack them into one.
		 *
		 * @param inputs The list of [RecipeInput]s.
		 * @return The list of unique inputs.
		 */
		fun uniqueIngredientsList(inputs: List<RecipeInput>): List<RecipeInput> {
			val list = ObjectArrayList<RecipeInput>(inputs.size)
			inputs.forEach { item ->
				var isEqual = false
				list.forEach {
					if(item.equalsIgnoreAmount(it)) {
						isEqual = true
						return@forEach
					}
				}
				if(isEqual) return@forEach
				list.add(item)
			}
			return list
		}

		/**
		 * Determine the correct root nodes for an ingredient
		 *
		 * @param ingredient the ingredient to check
		 * @param branchMap  the branch containing the nodes
		 * @return the correct nodes for the ingredient
		 */
		private fun determineRootNodes(ingredient: AbstractMapIngredient, branchMap: Branch): Object2ObjectOpenHashMap<AbstractMapIngredient, Either<Recipe, Branch>> {
			return if(ingredient.isSpecialIngredient) branchMap.specialNodes else branchMap.nodes
		}
	}

	private lateinit var recipeMap: RecipeMap<R>
	private val FLUID_INGREDIENT_ROOT = WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>()
	private val RECIPE_BY_CATEGORY = Object2ObjectOpenHashMap<RecipeCategory, MutableList<Recipe>>()

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
		if(recurseIngredientTreeAdd(recipe, items, lockup, 0, 0)) {
			RECIPE_BY_CATEGORY.compute(recipe.recipeCategory) { _, list ->
				if(list == null) listOf(recipe)
				else list.add(recipe)
				list
			}
			return true
		}
		return false
	}

	/**
	 * Converts a Recipe's [RecipeInput]s into a List of [AbstractMapIngredient]s
	 *
	 * @param recipe the recipe to use
	 * @return a list of all the AbstractMapIngredients comprising the recipe
	 */
	private fun fromRecipe(recipe: Recipe): List<List<AbstractMapIngredient>> {
		val list = ObjectArrayList<List<AbstractMapIngredient>>(recipe.inputs.size + recipe.fluidInputs.size)
		if(recipe.inputs.isNotEmpty())
			buildFromRecipeItems(list, uniqueIngredientsList(recipe.inputs))
		if(recipe.fluidInputs.isNotEmpty())
			buildFromRecipeFluids(list, recipe.fluidInputs)
		return list
	}

	/**
	 * Converts a list of [RecipeInput]s for Items into a List of [AbstractMapIngredient]s.
	 * Do not supply [RecipeInput]s dealing with any other type of input other than Items.
	 *
	 * @param list the list of MapIngredients to add to
	 * @param inputs the GTRecipeInputs to convert
	 */
	private fun buildFromRecipeItems(list: ObjectArrayList<List<AbstractMapIngredient>>, inputs: List<RecipeInput>) {
		inputs.forEach { it ->
			if(it.isOreDict()) {
				var ingredient: AbstractMapIngredient
				hasOreDictInputs = true
				if(it.hasNBTMatchingCondition()) {
					hasNBTMatcherInputs = true
					ingredient = MapOreDictNBTIngredient(it.getOreDict(), it.nbtMatcher, it.nbtCondition)
				} else
					ingredient = MapOreDictIngredient(it.getOreDict())
				// use the cached version if it exists
				retrieveCachedIngredient(list, ingredient, INGREDIENT_ROOT)
			} else {
				var ingredients: MutableList<AbstractMapIngredient>
				if(it.hasNBTMatchingCondition()) {
					ingredients = MapItemStackNBTIngredient.from(it)
					hasNBTMatcherInputs = true
				} else
					ingredients = MapItemStackIngredient.from(it)
				ingredients.indices.forEach { i ->
					val mappedIngredient = ingredients[i]
					// attempt to use the cached version if it exists, otherwise cache it
					val cached = INGREDIENT_ROOT.get(mappedIngredient)
					if(cached != null && cached.get() != null)
						ingredients[i] = cached.get()!!
					else
						INGREDIENT_ROOT[mappedIngredient] = WeakReference(mappedIngredient)
				}
				list.add(ingredients)
			}
		}
	}

	/**
	 * Converts a list of [RecipeInput]s for Fluids into a List of [AbstractMapIngredient]s.
	 * Do not supply [RecipeInput]s dealing with any other type of input other than Fluids.
	 *
	 * @param list the list of MapIngredients to add to
	 * @param fluidInputs the [RecipeInput]s to convert
	 */
	private fun buildFromRecipeFluids(list: ObjectArrayList<List<AbstractMapIngredient>>, fluidInputs: List<RecipeInput>) {
		fluidInputs.forEach {
			val ingredient = MapFluidIngredient(it)
			retrieveCachedIngredient(list, ingredient, FLUID_INGREDIENT_ROOT)
		}
	}

	/**
	 * Retrieves a cached ingredient, or inserts a default one
	 *
	 * @param list the list to append to
	 * @param defaultIngredient the ingredient to use as a default value, if not cached
	 * @param cache the ingredient root to retrieve from
	 */
	private fun retrieveCachedIngredient(
		list: ObjectArrayList<List<AbstractMapIngredient>>,
		defaultIngredient: AbstractMapIngredient,
		cache: WeakHashMap<AbstractMapIngredient, WeakReference<AbstractMapIngredient>>
	) {
		val cached = cache[defaultIngredient]
		if(cached != null && cached.get() != null)
			list.add(ObjectLists.singleton(cached.get()))
		else {
			cache[defaultIngredient] = WeakReference(defaultIngredient)
			list.add(ObjectLists.singleton(defaultIngredient))
		}
	}

	/**
	 * Adds a recipe to the map. (recursive part)
	 *
	 * TODO: actually document the algorithm as it tends to be confusing
	 *
	 * TODO: maybe roz can come up with a improved implementation or at least make it more readable
	 *
	 * @param recipe the recipe to add.
	 * @param ingredients list of input ingredients representing the recipe.
	 * @param branchMap the current branch in the recursion.
	 * @param index where in the ingredients list we are.
	 * @param count how many branches were added already.
	 */
	private fun recurseIngredientTreeAdd(recipe: Recipe, ingredients: List<List<AbstractMapIngredient>>, branchMap: Branch, index: Int, count: Int): Boolean {
		if(count >= ingredients.size) return true
		if(index >= ingredients.size) throw IllegalStateException("Index $index is out of bounds for ingredients list of size ${ingredients.size}")
		val current = ingredients[index]
		val branchRight = Branch()
		var r: Either<Recipe, Branch>
		current.forEach { ingredient ->
			val targetMap = determineRootNodes(ingredient, branchMap)
			r = targetMap.compute(ingredient) { _, existing ->
				if(count == ingredients.size - 1) {
					if(existing != null) {
						if(existing.left != null || existing.left != recipe) {
							if(recipe.groovyRecipe) {
								TODO("Handle Groovy Recipe")
							}
						}
						return@compute existing
					} else
						return@compute Either.left(recipe)
				} else if(existing == null)
					return@compute Either.right(branchRight)
				return@compute existing
			}!!
			if(r.left != null) {
				if(r.left == recipe)
					return@forEach
				else
					return false
			}
			val addedNextBranch: Boolean = r.right?.let { recurseIngredientTreeAdd(recipe, ingredients, it, (index + 1) % ingredients.size, count + 1) } == true
			if(!addedNextBranch) {
				if(count == ingredients.size - 1) {
					targetMap.remove(ingredient)
				} else {
					if(targetMap[ingredient]?.right != null)
						if(targetMap[ingredient]?.right?.empty ?: false)
							targetMap.remove(ingredient)
				}
				return false
			}
		}
		return true
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
		validator.error(
			recipe.outputs.size + recipe.chancedOutputs.chancedElements.size > maxOutputs,
			"Invalid amounts of item outputs. Recipe has ${recipe.outputs.size + recipe.chancedOutputs.chancedElements.size} item outputs, but the maximum is $maxOutputs."
		)
		validator.error(recipe.fluidInputs.size > maxFluidInputs, "Invalid amounts of fluid inputs. Recipe has ${recipe.fluidInputs.size} fluid inputs, but the maximum is $maxFluidInputs.")
		validator.error(
			recipe.fluidOutputs.size + recipe.chancedFluidOutputs.chancedElements.size > maxFluidOutputs,
			"Invalid amounts of fluid outputs. Recipe has ${recipe.fluidOutputs.size + recipe.chancedFluidOutputs.chancedElements.size} fluid outputs, but the maximum is $maxFluidOutputs."
		)
		validator.logMessages()
		return ValidationResult(validator.status, recipe)
	}
}
