@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun BlockPos.rotateX(degrees: Int): BlockPos {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad)
	val sin = sin(rad)
	return BlockPos(
		this.x,
		(this.y * cos - this.z * sin).roundToInt(),
		(this.y * sin + this.z * cos).roundToInt()
	)
}

fun BlockPos.rotateY(degrees: Int): BlockPos {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad)
	val sin = sin(rad)
	return BlockPos(
		(this.x * cos - this.z * sin).roundToInt(),
		this.y,
		(this.x * sin + this.z * cos).roundToInt()
	)
}

fun BlockPos.rotateZ(degrees: Int): BlockPos {
	val rad = Math.toRadians(degrees.toDouble())
	val cos = cos(rad)
	val sin = sin(rad)
	return BlockPos(
		(this.x * cos - this.y * sin).roundToInt(),
		(this.x * sin + this.y * cos).roundToInt(),
		this.z
	)
}

inline operator fun BlockPos.minus(other: BlockPos): BlockPos =
	subtract(other)

inline operator fun BlockPos.plus(other: BlockPos): BlockPos =
	add(other)

inline operator fun BlockPos.times(scalar: Int) =
	BlockPos(x * scalar, y * scalar, z * scalar)

inline fun Pair<BlockPos, BlockPos>.getAllInBox(): Iterable<BlockPos> =
	BlockPos.getAllInBox(first, second)

inline fun BlockPos.getFacingFromEntityPosition(entityX: Float, entityZ: Float): EnumFacing =
	EnumFacing.getFacingFromVector(entityX - x, 0f, entityZ - z)

inline fun BlockPos.getFacingFromEntity(entity: Entity): EnumFacing =
	getFacingFromEntityPosition(entity.posX.toFloat(), entity.posZ.toFloat())

/**
 * @see org.ender_development.catalyx.core.common.blocks.multiblock.parts.AbstractEdgeBlock
 */
fun BlockPos.getHorizontalSurroundings(radius: Int = 1): Array<BlockPos> {
	val positions = mutableListOf<BlockPos>()
	for (dz in -radius..radius) {
		for (dx in -radius..radius) {
			if (dx == 0 && dz == 0) continue
			positions.add(this.add(dx, 0, dz))
		}
	}
	return positions.toTypedArray()
}
