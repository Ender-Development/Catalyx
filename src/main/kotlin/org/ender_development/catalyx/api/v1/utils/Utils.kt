package org.ender_development.catalyx.api.v1.utils

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import org.ender_development.catalyx.api.v1.utils.interfaces.IBlockPosUtils
import org.ender_development.catalyx.api.v1.utils.interfaces.IEnvironmentUtils
import org.ender_development.catalyx.core.utils.BlockPosUtils
import org.ender_development.catalyx.core.utils.EnvironmentUtils

object Utils {
	val blockPos: IBlockPosUtils = BlockPosUtils

	val environment: IEnvironmentUtils = EnvironmentUtils

	// TODO find an abstraction
	/**
	 * [This can't be an extension as of right now there is no way to create a static extension of a JVM class.](https://youtrack.jetbrains.com/issue/KT-11968)
	 */
	@Suppress("ClassName")
	object fluidTank {
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
}
