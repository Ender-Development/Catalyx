package org.ender_development.catalyx.utils.math

import net.minecraft.util.math.BlockPos

object BlockPositions {
	/**
	 * Creates a wall shape centered at [center] with radius [r] and height [h].
	 *
	 * @param center The center position of the wall.
	 * @param r The radius from the center to the edges of the wall.
	 * @param h The height of the wall.
	 * @param offset An optional horizontal offset to apply to the wall's position.
	 * @param degrees The rotation angle in degrees to apply around the Y-axis.
	 * @return A pair of [Vec3] representing the minimum and maximum corners of the wall.
	 */
	fun wall(center: Vec3, r: Int, h: Int, offset: Int = 0, degrees: Int = 0): Pair<Vec3, Vec3> {
		val baseOrigin = Vec3(center.x - r, center.y, center.z + r + offset)
		val v1 = Vec3(2 * r, 0, 0)
		val v2 = Vec3(0, h, 0)

		val origin = RotateVec3.rotateY(baseOrigin - center, degrees) + center
		val v1Rot = RotateVec3.rotateY(v1, degrees)

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
	fun hollowCuboid(center: Vec3, r: Int, h: Int, offset: Int = 1): List<Pair<Vec3, Vec3>> = (0..3).map { i -> wall(center, r, h, offset, i * 90) }

	/**
	 * Gets all block positions within the axis-aligned bounding box defined by two corner points.
	 */
	fun getAllInBox(v1: Vec3, v2: Vec3): Iterable<BlockPos> = BlockPos.getAllInBox(v1.toBlockPos(), v2.toBlockPos())

	fun getAllInBox(pair: Pair<Vec3, Vec3>): Iterable<BlockPos> = getAllInBox(pair.first, pair.second)
}
