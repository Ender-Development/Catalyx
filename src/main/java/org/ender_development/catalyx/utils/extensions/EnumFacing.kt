package org.ender_development.catalyx.utils.extensions

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing

val EnumFacing.glRotationAngle: Float
	get() = when(this) {
		EnumFacing.NORTH -> 180f
		EnumFacing.EAST -> 90f
		EnumFacing.SOUTH -> 0f
		EnumFacing.WEST -> -90f
		EnumFacing.UP, EnumFacing.DOWN -> 0f
	}

val EnumFacing.glOffsetX
	get() = when(this) {
		EnumFacing.NORTH, EnumFacing.SOUTH -> .5
		EnumFacing.WEST -> 1.0
		else -> .0
	}

val EnumFacing.glOffsetZ
	get() = when(this) {
		EnumFacing.NORTH -> 1.0
		EnumFacing.EAST, EnumFacing.WEST -> .5
		else -> .0
	}

fun EnumFacing.glRotate() =
	when(this) {
		EnumFacing.NORTH -> GlStateManager.rotate(180f, 0f, 1f, 0f)
		EnumFacing.EAST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
		EnumFacing.SOUTH -> {}
		EnumFacing.WEST -> GlStateManager.rotate(-90f, 0f, 1f, 0f)
		EnumFacing.UP -> GlStateManager.rotate(-90f, 1f, 0f, 0f)
		EnumFacing.DOWN -> GlStateManager.rotate(90f, 1f, 0f, 0f)
	}
