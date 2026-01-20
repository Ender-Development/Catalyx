package org.ender_development.catalyx.api.v1.utils.interfaces

import net.minecraft.util.math.BlockPos

interface IBlockPosUtils {
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
	fun wall(center: BlockPos, r: Int, h: Int, offset: Int = 0, degrees: Int = 0, shrink: Int = 0): Pair<BlockPos, BlockPos>

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
	fun hollowCuboid(center: BlockPos, r: Int, h: Int, offset: Int = 1, shrink: Int = 1): List<Pair<BlockPos, BlockPos>>
}
