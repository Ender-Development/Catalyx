package org.ender_development.catalyx.utils.extensions

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import java.awt.Color

fun FluidStack.getColor() =
	fluid.getColor(this)

private val waterColour = Color.blue.rgb
private val lavaColour = Color(0x81, 0x3d, 0x0e).rgb
internal fun FluidStack.getRealColor() =
	when(fluid) {
		FluidRegistry.WATER -> waterColour
		FluidRegistry.LAVA -> lavaColour
		else -> getColor()
	}
