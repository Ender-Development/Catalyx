@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.utils.math.BlockPosRotate
import scala.inline

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

inline fun BlockPos.getFacingFromEntityPosition(entityX: Double, entityZ: Double) =
	EnumFacing.getFacingFromVector((entityX - this.x).toFloat(), 0f, (entityZ - this.z).toFloat())

inline fun BlockPos.getFacingFromEntity(entity: Entity): EnumFacing =
	getFacingFromEntityPosition(entity.posX, entity.posZ)

/**
 * @see org.ender_development.catalyx.blocks.multiblock.parts.AbstractEdgeBlock
 */
	inline

fun BlockPos.getHorizontalSurroundings() = arrayOf(
	north().west(), north(), north().east(),
	west(),        /* us */  east(),
	south().west(), south(), south().east()
)
