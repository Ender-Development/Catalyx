package org.ender_development.catalyx.core.utils

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank

object FluidTankUtils {
	// These cannot be an extension as there's currently no way to create a static extension for a JVM class afact (see https://youtrack.jetbrains.com/issue/KT-11968)

	inline fun create(tile: TileEntity, capacity: Int, canFill: Boolean, canDrain: Boolean, crossinline onContentsChangedCallback: () -> Unit) =
		object : FluidTank(capacity) {
			init {
				setTileEntity(tile)
				setCanFill(canFill)
				setCanDrain(canDrain)
			}

			override fun onContentsChanged() =
				onContentsChangedCallback()
		}

	inline fun create(tile: TileEntity, capacity: Int, canFill: Boolean, canDrain: Boolean, vararg fluidWhitelist: Fluid, crossinline onContentsChangedCallback: () -> Unit) =
		object : FluidTank(capacity) {
			init {
				setTileEntity(tile)
				setCanFill(canFill)
				setCanDrain(canDrain)
			}

			override fun onContentsChanged() =
				onContentsChangedCallback()

			override fun canFillFluidType(fluid: FluidStack?) =
				fluid != null && fluidWhitelist.any { fluid.fluid === it }

			override fun canDrainFluidType(fluid: FluidStack?) =
				canFillFluidType(fluid)
		}
}
