package org.ender_development.catalyx.core.utils.math

import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.core.utils.extensions.minus
import org.ender_development.catalyx.core.utils.extensions.plus
import org.ender_development.catalyx.core.utils.extensions.rotateY

object BlockPosUtils {
	/**
	 * Creates a wall shape centered at [center] with radius [r] and height [h].
	 *
	 * @param center The center position of the wall.
	 * @param r The radius from the center to the edges of the wall.
	 * @param h The height of the wall.
	 * @param offset An optional horizontal offset to apply to the wall's position.
	 * @param degrees The rotation angle in degrees to apply around the Y-axis.
	 * @param shrink Reduces wall width by shrink blocks on its far end to avoid corner overlaps.
	 * @return A [Pair] of [BlockPos] representing the minimum and maximum corners of the wall.
	 */
	fun wall(center: BlockPos, r: Int, h: Int, offset: Int = 0, degrees: Int = 0, shrink: Int = 0): Pair<BlockPos, BlockPos> {
		val baseOrigin = BlockPos(center.x - r, center.y, center.z + r + offset)
		val v1 = BlockPos(2 * r - shrink, 0, 0)
		val v2 = BlockPos(0, h, 0)

		val origin = (baseOrigin - center).rotateY(degrees) + center
		val v1Rot = v1.rotateY(degrees)

		val corners = listOf(
			origin,
			origin + v1Rot,
			origin + v2,
			origin + v1Rot + v2
		)

		return BlockPos(corners.minOf { it.x }, corners.minOf { it.y }, corners.minOf { it.z }) to BlockPos(corners.maxOf { it.x }, corners.maxOf { it.y }, corners.maxOf { it.z })
	}

	/**
	 * Creates a hollow cuboid shape centered at [center] with radius [r] and height [h].
	 * The cuboid is constructed by creating four walls around the center point.
	 *
	 * @param center The center position of the cuboid.
	 * @param r The radius from the center to the edges of the cuboid.
	 * @param h The height of the cuboid.
	 * @param offset An optional vertical offset to apply to the base of the cuboid.
	 * @return A [List] of [Pair]`s` of [BlockPos] representing the minimum and maximum corners of each wall.
	 */
	fun hollowCuboid(center: BlockPos, r: Int, h: Int, offset: Int = 1, shrink: Int = 1) =
		(0..3).map {
			wall(center, r, h, offset, it * 90, shrink)
		}
}
