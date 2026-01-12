package org.ender_development.catalyx.core.recipes

import com.cleanroommc.groovyscript.api.GroovyLog
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Optional
import org.ender_development.catalyx.core.recipes.chance.output.ChancedFluidOutput
import org.ender_development.catalyx.core.recipes.chance.output.ChancedItemOutput
import org.ender_development.catalyx.core.recipes.chance.output.ChancedOutputList
import org.ender_development.catalyx.core.recipes.chance.output.IChancedOutputLogic
import org.ender_development.catalyx.core.recipes.ingredients.RecipeInput
import org.ender_development.catalyx.core.recipes.validation.Result
import org.ender_development.catalyx.core.recipes.validation.ValidationState
import org.ender_development.catalyx.core.recipes.validation.Validator
import org.ender_development.catalyx.core.utils.Mods
import org.ender_development.catalyx.modules.integration.groovyscript.ModuleGroovyScript
import java.util.function.Supplier

class RecipeBuilder<R : RecipeBuilder<R>> {
	companion object {
		fun getRequiredString(max: Int, found: Int, type: String): String {
			if(max <= 0)
				return "No ${type}s allowed, but found $found"
			var out = "Must have at most $max $type"
			if(max != 1)
				out += "s"
			return "$out, but found $found"
		}
	}

	lateinit var recipeMap: RecipeMap<R>

	var inputs: MutableList<RecipeInput?>
	var outputs: MutableList<ItemStack?>
	var chancedOutputs: MutableList<ChancedItemOutput?>

	var fluidInputs: MutableList<RecipeInput?>
	var fluidOutputs: MutableList<FluidStack?>
	var chancedFluidOutputs: MutableList<ChancedFluidOutput?>

	var chancedOutputLogic: IChancedOutputLogic = IChancedOutputLogic.OR
	var chancedFluidOutputLogic: IChancedOutputLogic = IChancedOutputLogic.OR

	var duration: Int = 0
	var energyPerTick: Long = 0
	var hidden: Boolean = false
	var category: RecipeCategory? = null
	var parallel: Int = 0
	var recipeStatus: ValidationState = ValidationState.VALID

	private constructor() {
		this.inputs = ArrayList<RecipeInput?>()
		this.outputs = ArrayList<ItemStack?>()
		this.chancedOutputs = ArrayList<ChancedItemOutput?>()
		this.fluidInputs = ArrayList<RecipeInput?>()
		this.fluidOutputs = ArrayList<FluidStack?>()
		this.chancedFluidOutputs = ArrayList<ChancedFluidOutput?>()
	}

	constructor(recipe: Recipe, recipeMap: RecipeMap<R>) {
		this.recipeMap = recipeMap
		this.inputs = recipe.inputs.toMutableList()
		this.outputs = recipe.outputs.toMutableList()
		this.chancedOutputs = recipe.chancedOutputs.chancedElements.toMutableList()
		this.fluidInputs = recipe.fluidInputs.toMutableList()
		this.fluidOutputs = recipe.fluidOutputs.toMutableList()
		this.chancedFluidOutputs = recipe.chancedFluidOutputs.chancedElements.toMutableList()
		this.duration = recipe.duration
		this.energyPerTick = recipe.energyPerTick
		this.hidden = recipe.hidden
		this.category = recipe.recipeCategory
	}

	fun build() =
		Result(
			validate(), Recipe(
				inputs, outputs.filterNotNull(), ChancedOutputList(chancedOutputLogic, chancedOutputs.filterNotNull()),
				fluidInputs, fluidOutputs.filterNotNull(), ChancedOutputList(chancedFluidOutputLogic, chancedFluidOutputs.filterNotNull()),
				duration, energyPerTick, hidden, category!!
			)
		)

	private fun validate(): ValidationState {
		val validator = Validator()
		if(ModuleGroovyScript.isRunning) {
			val msg = GroovyLog.msg("Error adding recipe: ${recipeMap.unlocalizedName}").error()
			validateGroovy(msg)
			return if(msg.postIfNotEmpty()) ValidationState.SKIP else ValidationState.VALID
		}
		validator.assert(duration > 0, "Duration must be greater than 0")
		validator.assert(energyPerTick != 0L, "Energy per tick must not be 0")
		validator.assert(category != null, "Recipe category must be set")
		validator.assert(category?.recipeMap == recipeMap, "Recipe category does not belong to the recipe map")
		validator.logErrors(initialMsg = "Invalid recipe for ${recipeMap.unlocalizedName}:")
		return validator.status
	}

	@Optional.Method(modid = Mods.GROOVYSCRIPT)
	private fun validateGroovy(errorMsg: GroovyLog.Msg) {
		errorMsg.add(energyPerTick == 0L, Supplier { "Energy per tick must not be to 0" })
		errorMsg.add(duration <= 0, Supplier { "Duration must not be less or equal to 0" })
		val maxInput = recipeMap.maxInputs
		val maxOutput = recipeMap.maxOutputs
		val maxFluidInput = recipeMap.maxFluidInputs
		val maxFluidOutput = recipeMap.maxFluidOutputs
		errorMsg.add(inputs.size > maxInput, Supplier { getRequiredString(maxInput, inputs.size, "item input") })
		errorMsg.add(outputs.size > maxOutput, Supplier { getRequiredString(maxOutput, outputs.size, "item output") })
		errorMsg.add(fluidInputs.size > maxFluidInput, Supplier { getRequiredString(maxFluidInput, fluidInputs.size, "fluid input") })
		errorMsg.add(fluidOutputs.size > maxFluidOutput, Supplier { getRequiredString(maxFluidOutput, fluidOutputs.size, "fluid output") })
	}

	/**
	 * Build and register the recipe, if valid.
	 * Do not call outside the registry event for [net.minecraft.item.crafting.IRecipe].
	 * @see net.minecraftforge.event.RegistryEvent.Register<IRecipe>
	 */
	fun buildAndRegister() {
		val result = build()
		recipeMap.addRecipe(result)
	}
}
