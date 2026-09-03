package org.ender_development.catalyx.api.v1.common.tileentities.interfaces

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.core.common.blocks.IOType

interface IPortRenderer {
	fun getPortState(face: EnumFacing): IOType
}
