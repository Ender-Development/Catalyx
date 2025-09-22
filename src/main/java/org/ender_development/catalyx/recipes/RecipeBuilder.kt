package org.ender_development.catalyx.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.recipes.chance.output.ChancedFluidOutput
import org.ender_development.catalyx.recipes.chance.output.ChancedItemOutput
import org.ender_development.catalyx.recipes.chance.output.IChancedOutputLogic
import org.ender_development.catalyx.recipes.ingredients.RecipeInput

class RecipeBuilder<R: RecipeBuilder<R>> {
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
	var recipeStatus: ValidationStatus? = ValidationStatus.VALID

	private constructor() {
		this.inputs = ArrayList<RecipeInput?>()
		this.outputs = ArrayList<ItemStack?>()
		this.chancedOutputs = ArrayList<ChancedItemOutput?>()
		this.fluidInputs = ArrayList<RecipeInput?>()
		this.fluidOutputs = ArrayList<FluidStack?>()
		this.chancedFluidOutputs = ArrayList<ChancedFluidOutput?>()
	}

	constructor(recipe: Recipe, recipeMap: RecipeMap<R>?) {
		this.recipeMap = recipeMap!!
		this.inputs = recipe.inputs.toMutableList()
		this.outputs = recipe.outputs.toMutableList()
		this.chancedOutputs = recipe.chancedOutputs.chancedElements.toMutableList()
		this.fluidInputs = recipe.fluidInputs.toMutableList()
		this.fluidOutputs = recipe.fluidOutputs.toMutableList()
		this.chancedFluidOutputs = recipe.chancedFluidOutput.chancedElements.toMutableList()
		this.duration = recipe.duration
		this.energyPerTick = recipe.energyPerTick
		this.hidden = recipe.hidden
		this.category = recipe.recipeCategory
	}
}
