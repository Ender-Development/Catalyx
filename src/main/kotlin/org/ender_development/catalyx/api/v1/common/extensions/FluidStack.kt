@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import java.awt.Color

inline fun FluidStack.getColor() =
	fluid.getColor(this)

private val waterColor = Color(0x20, 0x40, 0xff).rgb
private val lavaColor = Color(0x81, 0x3d, 0x0e).rgb
/**
 * @returns the color of a given Fluid, with some sensible defaults for Water and Lava, which don't have a color set (thank Forge for that)
 */
fun FluidStack.getRealColor() =
	when(fluid) {
		FluidRegistry.WATER -> waterColor
		FluidRegistry.LAVA -> lavaColor
		else -> getColor()
	}

/**
 * Convert a fluid stack to an identifier string, respecting the NBT data
 */
inline fun FluidStack.nbtString() =
	"${this.amount}x${this.unlocalizedName}" + (this.tag?.toSortedString() ?: "")
