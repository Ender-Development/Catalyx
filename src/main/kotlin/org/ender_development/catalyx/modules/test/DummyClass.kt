package org.ender_development.catalyx.modules.test

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.common.blocks.IOType
import org.ender_development.catalyx.core.common.tileentities.multiblock.CenterTile
import org.ender_development.catalyx.core.common.tileentities.IOTile

internal class DummyClass1 : CenterTile()
internal class DummyClass2 : IOTile(Catalyx) {
	override fun getPortState(face: EnumFacing): IOType = when(face) {
		EnumFacing.NORTH -> IOType.INPUT
		EnumFacing.EAST  -> IOType.PULL
		EnumFacing.SOUTH -> IOType.OUTPUT
		EnumFacing.WEST  -> IOType.PUSH
		EnumFacing.UP -> IOType.NONE
		EnumFacing.DOWN -> IOType.DEFAULT
	}
}
