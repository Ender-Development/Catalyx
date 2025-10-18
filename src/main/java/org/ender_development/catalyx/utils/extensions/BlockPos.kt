package org.ender_development.catalyx.utils.extensions

import net.minecraft.util.math.BlockPos
import org.ender_development.catalyx.utils.math.Vec3

fun BlockPos.getAllInBox(v1: Vec3, v2: Vec3): Iterable<BlockPos> = BlockPos.getAllInBox(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z)

fun BlockPos.getAllInBox(pair: Pair<Vec3, Vec3>): Iterable<BlockPos> = getAllInBox(pair.first, pair.second)
