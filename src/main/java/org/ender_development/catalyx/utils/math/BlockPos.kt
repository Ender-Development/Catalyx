package org.ender_development.catalyx.utils.math

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Vec3(val x: Int, val y: Int, val z: Int) {
	operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
	operator fun times(scalar: Int) = Vec3(x * scalar, y * scalar, z * scalar)
}

fun rotateY(v: Vec3, degrees: Int): Vec3 {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad).roundToInt()
	val sin = sin(rad).roundToInt()
	return Vec3(
		v.x * cos - v.z * sin,
		v.y,
		v.x * sin + v.z * cos
	)
}

fun rotateX(v: Vec3, degrees: Int): Vec3 {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad).roundToInt()
	val sin = sin(rad).roundToInt()
	return Vec3(
		v.x,
		v.y * cos - v.z * sin,
		v.y * sin + v.z * cos
	)
}

fun rotateZ(v: Vec3, degrees: Int): Vec3 {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad).roundToInt()
	val sin = sin(rad).roundToInt()
	return Vec3(
		v.x * cos - v.y * sin,
		v.x * sin + v.y * cos,
		v.z
	)
}

/**
 * Creates a wall shape centered at [center] with radius [r] and height [h].
 *
 * @param center The center position of the wall.
 * @param r The radius from the center to the edges of the wall.
 * @param h The height of the wall.
 * @param offset An optional vertical offset to apply to the base of the wall.
 * @param degrees The rotation angle in degrees to apply around the Y-axis.
 * @return A pair of [Vec3] representing the minimum and maximum corners of the wall.
 */
fun wall(center: Vec3, r: Int, h: Int, offset: Int = 0, degrees: Int = 0): Pair<Vec3, Vec3> {
	val baseOrigin = Vec3(center.x - r, center.y, center.z + r + offset)
	val v1 = Vec3(2 * r, 0, 0)
	val v2 = Vec3(0, h, 0)

	val origin = rotateY(baseOrigin - center, degrees) + center
	val v1Rot = rotateY(v1, degrees)

	val corners = listOf(
		origin,
		origin + v1Rot,
		origin + v2,
		origin + v1Rot + v2
	)

	return Pair(Vec3(corners.minOf { it.x }, corners.minOf { it.y }, corners.minOf { it.z }), Vec3(corners.maxOf { it.x }, corners.maxOf { it.y }, corners.maxOf { it.z }))
}

/**
 * Creates a hollow cuboid shape centered at [center] with radius [r] and height [h].
 * The cuboid is constructed by creating four walls around the center point.
 *
 * @param center The center position of the cuboid.
 * @param r The radius from the center to the edges of the cuboid.
 * @param h The height of the cuboid.
 * @param offset An optional vertical offset to apply to the base of the cuboid.
 * @return A list of pairs of [Vec3] representing the minimum and maximum corners of each wall.
 */
fun hollowCuboid(center: Vec3, r: Int, h: Int): List<Pair<Vec3, Vec3>> =
	(0..3).map { i -> wall(center, r, h, 1, i * 90)
}
