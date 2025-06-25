package io.enderdev.catalyx.client.gui.wrappers

import net.minecraftforge.fluids.IFluidTank
import java.text.NumberFormat
import java.util.Locale

open class CapabilityFluidDisplayWrapper(x: Int, y: Int, width: Int, height: Int, val fluidTank: () -> IFluidTank) : CapabilityDisplayWrapper(x, y, width, height) {
	override fun getCapacity() = fluidTank().capacity
	override fun getStored() = fluidTank().fluidAmount

	private val numFormat = NumberFormat.getInstance(Locale.getDefault())

	override fun toStringList() = listOf("${numFormat.format(getStored())}/${numFormat.format(getCapacity())} mB ${fluidTank().fluid?.fluid?.getLocalizedName(fluidTank().fluid) ?: ""}".trimEnd())

	fun getFluid() = fluidTank().fluid
}
