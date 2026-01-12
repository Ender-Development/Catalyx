package org.ender_development.catalyx.core.utils.math

import net.minecraft.util.math.BlockPos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object BlockPosRotate {
	fun rotateX(vec: BlockPos, degrees: Int): BlockPos {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad)
		val sin = sin(rad)
		return BlockPos(
			vec.x,
			(vec.y * cos - vec.z * sin).roundToInt(),
			(vec.y * sin + vec.z * cos).roundToInt()
		)
	}

	fun rotateY(vec: BlockPos, degrees: Int): BlockPos {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad)
		val sin = sin(rad)
		return BlockPos(
			(vec.x * cos - vec.z * sin).roundToInt(),
			vec.y,
			(vec.x * sin + vec.z * cos).roundToInt()
		)
	}

	fun rotateZ(vec: BlockPos, degrees: Int): BlockPos {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad)
		val sin = sin(rad)
		return BlockPos(
			(vec.x * cos - vec.y * sin).roundToInt(),
			(vec.x * sin + vec.y * cos).roundToInt(),
			vec.z
		)
	}
}
