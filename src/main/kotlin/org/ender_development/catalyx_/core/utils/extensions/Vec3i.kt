@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx_.core.utils.extensions

import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

inline fun Vec3i.getCentre() =
	Vec3d(x + .5, y + .5, z + .5)

inline operator fun Vec3i.component1() =
	x

inline operator fun Vec3i.component2() =
	y

inline operator fun Vec3i.component3() =
	z
