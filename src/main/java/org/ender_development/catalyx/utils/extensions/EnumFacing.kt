package org.ender_development.catalyx.utils.extensions

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing

fun EnumFacing.glRotate() =
	when(this) {
		EnumFacing.NORTH -> GlStateManager.rotate(180f, 0f, 1f, 0f)
		EnumFacing.EAST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
		EnumFacing.SOUTH -> {}
		EnumFacing.WEST -> GlStateManager.rotate(-90f, 0f, 1f, 0f)
		// TODO up & down
		else -> {}
	}
