package org.ender_development.catalyx.utils.math

import net.minecraft.util.math.BlockPos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Vec3(val x: Int, val y: Int, val z: Int) {
	operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
	operator fun times(scalar: Int) = Vec3(x * scalar, y * scalar, z * scalar)
}

object RotateVec3 {
	fun rotateY(vec: Vec3, degrees: Int): Vec3 {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad).roundToInt()
		val sin = sin(rad).roundToInt()
		return Vec3(
			vec.x * cos - vec.z * sin,
			vec.y,
			vec.x * sin + vec.z * cos
		)
	}

	fun rotateX(vec: Vec3, degrees: Int): Vec3 {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad).roundToInt()
		val sin = sin(rad).roundToInt()
		return Vec3(
			vec.x,
			vec.y * cos - vec.z * sin,
			vec.y * sin + vec.z * cos
		)
	}

	fun rotateZ(vec: Vec3, degrees: Int): Vec3 {
		val rad = Math.toRadians(degrees.toDouble())
		val cos = cos(rad).roundToInt()
		val sin = sin(rad).roundToInt()
		return Vec3(
			vec.x * cos - vec.y * sin,
			vec.x * sin + vec.y * cos,
			vec.z
		)
	}
}

fun Vec3.toBlockPos() = BlockPos(x, y, z)

fun Vec3.rotateY(degrees: Int): Vec3 = RotateVec3.rotateY(this, degrees)

fun Vec3.rotateX(degrees: Int): Vec3 = RotateVec3.rotateX(this, degrees)

fun Vec3.rotateZ(degrees: Int): Vec3 = RotateVec3.rotateZ(this, degrees)
