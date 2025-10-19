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

inline fun BlockPos.getHorizontalSurroundings() = arrayOf(
	this.north(),
	this.north().west(),
	this.west(),
	this.south().west(),
	this.south(),
	this.south().east(),
	this.east(),
	this.north().east()
)

inline fun BlockPos.getHorizontalCenterFromMeta(meta: Int): BlockPos = when(meta.coerceAtMost(7)) {
	0 -> this.south()
	1 -> this.south().east()
	2 -> this.east()
	3 -> this.north().east()
	4 -> this.north()
	5 -> this.north().west()
	6 -> this.west()
	7 -> this.south().west()
	else -> this
}
