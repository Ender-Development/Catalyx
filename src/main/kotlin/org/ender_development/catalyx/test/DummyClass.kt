package org.ender_development.catalyx.test

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.blocks.helper.IOType
import org.ender_development.catalyx.tiles.CenterTile
import org.ender_development.catalyx.tiles.IOTile

class DummyClass1 : CenterTile()
class DummyClass2 : IOTile(Catalyx) {
	override fun getPortState(face: EnumFacing): IOType = when(face) {
		EnumFacing.NORTH -> IOType.INPUT
		EnumFacing.EAST  -> IOType.PULL
		EnumFacing.SOUTH -> IOType.OUTPUT
		EnumFacing.WEST  -> IOType.PUSH
		EnumFacing.UP -> IOType.NONE
		EnumFacing.DOWN -> IOType.DEFAULT
	}
}
