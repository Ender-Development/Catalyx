package org.ender_development.catalyx.tiles.helper

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx_.core.blocks.helper.IOType
import org.ender_development.catalyx.tiles.TESRTile

interface IPortRenderer {
	fun getPortState(face: EnumFacing): IOType
}
