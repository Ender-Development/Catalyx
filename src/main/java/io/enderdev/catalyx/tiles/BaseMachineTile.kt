package io.enderdev.catalyx.tiles

import io.enderdev.catalyx.CatalyxSettings
import io.enderdev.catalyx.client.button.PauseButton
import io.enderdev.catalyx.client.button.RedstoneButton
import io.enderdev.catalyx.tiles.helper.IButtonTile
import io.enderdev.catalyx.tiles.helper.IGuiTile
import io.enderdev.catalyx.tiles.helper.IItemTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

/**
 * A base Catalyx TileEntity with functions allowing you to create custom machines efficiently
 */
abstract class BaseMachineTile<T>(settings: CatalyxSettings) : BaseTile(settings), ITickable, IGuiTile, IItemTile, IButtonTile {
	abstract val recipeTime: Int
	abstract val energyPerTick: Int

	var progressTicks = 0
	var isPaused = false
	var needsRedstonePower = false
	var currentRecipe: T? = null

	/**
	 * Update the stored recipe variable.
	 * Used in init and readFromNBT.
	 */
	abstract fun updateRecipe()

	/**
	 * Fired when the machine finishes a recipe.
	 * Used for consuming inputs and producing outputs.
	 */
	abstract fun onProcessComplete()

	/**
	 * Fired every tick the machine is active.
	 * Used for consuming energy, etc.
	 */
	abstract fun onWorkTick()

	/**
	 * Check if there is anything present to process.
	 */
	abstract fun shouldTick(): Boolean

	/**
	 * Check if the machine should process the current recipe.
	 */
	abstract fun shouldProcess(): Boolean

	/**
	 * Called every tick when the machine is idle.
	 * Used for updating the recipe, etc.
	 */
	open fun onIdleTick() = updateRecipe()

	override fun update() {
		if(world.isRemote) return
		markDirtyGUIEvery(5)

		if(isPaused || needsRedstonePower != this.world.isBlockPowered(this.pos)) return
		if(!shouldTick()) {
			progressTicks = 0
			return
		}
		onIdleTick()
		if(currentRecipe == null || !shouldProcess()) {
			progressTicks = 0
			return
		}
		onWorkTick()
		if(progressTicks++ == recipeTime) {
			progressTicks = 0
			onProcessComplete()
		}
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setBoolean("IsPaused", isPaused)
		compound.setBoolean("NeedsPower", needsRedstonePower)
		compound.setInteger("ProgressTicks", progressTicks)
		return compound
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		isPaused = compound.getBoolean("IsPaused")
		needsRedstonePower = compound.getBoolean("NeedsPower")
		progressTicks = compound.getInteger("ProgressTicks")
	}

	override fun handleButtonPress(id: Int) {
		if(id == PauseButton.buttonId)
			isPaused = !isPaused
		else if(id == RedstoneButton.buttonId)
			needsRedstonePower = !needsRedstonePower
	}
}
