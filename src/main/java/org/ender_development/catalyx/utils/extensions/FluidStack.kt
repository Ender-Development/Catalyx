package org.ender_development.catalyx.utils.extensions

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import java.awt.Color

fun FluidStack.getColor() =
	fluid.getColor(this)

private val waterColour = Color.blue.rgb
private val lavaColour = Color.orange.rgb
internal fun FluidStack.getRealColor() =
	when(fluid) {
		FluidRegistry.WATER -> waterColour
		FluidRegistry.LAVA -> lavaColour
		else -> getColor()
	}
