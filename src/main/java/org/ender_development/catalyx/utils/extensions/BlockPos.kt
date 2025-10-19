@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.utils.math.BlockPosRotate

inline fun BlockPos.rotateX(degrees: Int) =
	BlockPosRotate.rotateX(this, degrees)

inline fun BlockPos.rotateY(degrees: Int) =
	BlockPosRotate.rotateY(this, degrees)

inline fun BlockPos.rotateZ(degrees: Int) =
	BlockPosRotate.rotateZ(this, degrees)

inline operator fun BlockPos.minus(other: BlockPos): BlockPos =
	subtract(other)

inline operator fun BlockPos.plus(other: BlockPos): BlockPos =
	add(other)

inline operator fun BlockPos.times(scalar: Int) =
	BlockPos(x * scalar, y * scalar, z * scalar)

inline fun Pair<BlockPos, BlockPos>.getAllInBox() =
	BlockPos.getAllInBox(first, second)

/**
 * @see org.ender_development.catalyx.blocks.multiblock.BaseEdge
 * @see getHorizontalCenterFromMeta
 */
inline fun BlockPos.getHorizontalSurroundings() = arrayOf(
	north().west(), north(), north().east(),
	west(),        /* us */  east(),
	south().west(), south(), south().east()
)

/**
 * Inverse of the order in [getHorizontalSurroundings]
 * @see org.ender_development.catalyx.blocks.multiblock.BaseEdge
 * @see getHorizontalSurroundings
 */
fun BlockPos.getHorizontalCenterFromMeta(meta: Int): BlockPos = when(meta.coerceIn(0, 7)) {
	0 -> south().east()
	1 -> south()
	2 -> south().west()
	3 -> east()
	4 -> west()
	5 -> north().east()
	6 -> north()
	7 -> north().west()
	else -> error("physically cannot happen")
}
