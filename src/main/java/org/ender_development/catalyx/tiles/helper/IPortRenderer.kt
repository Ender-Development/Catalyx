package org.ender_development.catalyx.tiles.helper

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.tiles.TesrTile

interface IPortRenderer {
	fun getPortState(): Map<EnumFacing, TesrTile.IOType>
}
