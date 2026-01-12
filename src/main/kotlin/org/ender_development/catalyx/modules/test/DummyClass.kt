package org.ender_development.catalyx.modules.test

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.blocks.helper.IOType
import org.ender_development.catalyx.core.tiles.CenterTile
import org.ender_development.catalyx.core.tiles.IOTile

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
