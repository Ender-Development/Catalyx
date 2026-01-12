package org.ender_development.catalyx.core.tiles.helper

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.core.blocks.helper.IOType

interface IPortRenderer {
	fun getPortState(face: EnumFacing): IOType
}
