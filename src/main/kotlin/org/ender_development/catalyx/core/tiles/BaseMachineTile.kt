package org.ender_development.catalyx.core.tiles

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import org.ender_development.catalyx.core.client.button.AbstractButtonWrapper
import org.ender_development.catalyx.core.client.button.PauseButtonWrapper
import org.ender_development.catalyx.core.client.button.RedstoneButtonWrapper
import org.ender_development.catalyx.core.client.gui.BaseGuiTyped
import org.ender_development.catalyx.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx.core.tiles.helper.IButtonTile
import org.ender_development.catalyx.core.tiles.helper.IGuiTile
import org.ender_development.catalyx.core.tiles.helper.IItemTile

/**
 * A base Catalyx TileEntity with functions allowing you to create custom machines efficiently
 */
abstract class BaseMachineTile<T>(mod: ICatalyxMod) : BaseTile(mod), ITickable, IGuiTile, IItemTile, IButtonTile, BaseGuiTyped.IDefaultButtonVariables {
	abstract val recipeTime: Int
	abstract val energyPerTick: Int

	var progressTicks = 0
	override var isPaused = false
	override var needsRedstonePower = false
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
	 * Check if the recipe progress should reset if shouldProcess() is false
	 */
	open fun shouldResetProgress() = true

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
		if(currentRecipe == null) {
			progressTicks = 0
			return
		}
		if(!shouldProcess()) {
			if(shouldResetProgress())
				progressTicks = 0
			return
		}
		onWorkTick()
		if(progressTicks++ >= recipeTime) {
			progressTicks -= recipeTime
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

	override fun handleButtonPress(button: AbstractButtonWrapper) {
		if(button is PauseButtonWrapper)
			isPaused = !isPaused
		else if(button is RedstoneButtonWrapper)
			needsRedstonePower = !needsRedstonePower
		markDirtyGUI()
	}
}
