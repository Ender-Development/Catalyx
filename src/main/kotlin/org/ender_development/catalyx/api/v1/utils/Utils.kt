package org.ender_development.catalyx.api.v1.utils

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import org.ender_development.catalyx.api.v1.utils.interfaces.IBlockPosUtils
import org.ender_development.catalyx.api.v1.utils.interfaces.IFluidTankUtils
import org.ender_development.catalyx.api.v1.utils.interfaces.INetworkUtils
import org.ender_development.catalyx.core.utils.BlockPosUtils
import org.ender_development.catalyx.core.utils.NetworkUtils

object Utils {
	val forBlockPos: IBlockPosUtils =
		BlockPosUtils

	val forNetwork: INetworkUtils =
		NetworkUtils

	// bruh...
	// TODO fund am abstraction
	val forFluidTanks = object : IFluidTankUtils {
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
}
