package org.ender_development.catalyx_.core.client.gui.wrappers

import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank

open class CapabilityFluidDisplayWrapper(x: Int, y: Int, width: Int, height: Int, val fluidTank: () -> IFluidTank) : CapabilityDisplayWrapper(x, y, width, height) {
	override val capacity: Int
		get() = fluidTank().capacity

	override val stored: Int
		get() = fluidTank().fluidAmount

	val fluid: FluidStack?
		get() = fluidTank().fluid

	override val textLines: List<String>
		get() = listOf("${numFormat.format(stored)}/${numFormat.format(capacity)} mB${fluidTank().fluid?.fluid?.getLocalizedName(fluid)?.let { " $it" } ?: ""}")
}
