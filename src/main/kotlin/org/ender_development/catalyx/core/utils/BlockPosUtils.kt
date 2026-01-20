package org.ender_development.catalyx.core.utils

import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.api.v1.common.extensions.minus
import org.ender_development.catalyx.api.v1.common.extensions.plus
import org.ender_development.catalyx.api.v1.common.extensions.rotateY
import org.ender_development.catalyx.api.v1.utils.interfaces.IBlockPosUtils

object BlockPosUtils : IBlockPosUtils {
	override fun wall(center: BlockPos, r: Int, h: Int, offset: Int, degrees: Int, shrink: Int): Pair<BlockPos, BlockPos> {
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

	override fun hollowCuboid(center: BlockPos, r: Int, h: Int, offset: Int, shrink: Int) =
		(0..3).map {
			wall(center, r, h, offset, it * 90, shrink)
		}
}
