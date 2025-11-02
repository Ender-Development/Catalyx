@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import java.awt.Color

inline fun FluidStack.getColor() =
	fluid.getColor(this)

private val waterColour = Color(0x20, 0x40, 0xff).rgb
private val lavaColour = Color(0x81, 0x3d, 0x0e).rgb
/**
 * @returns the colour of a given Fluid, with some sensible defaults for Water and Lava, which don't have a colour set (thank Forge for that)
 */
fun FluidStack.getRealColor() =
	when(fluid) {
		FluidRegistry.WATER -> waterColour
		FluidRegistry.LAVA -> lavaColour
		else -> getColor()
	}
