@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.util.math.Vec3d

inline operator fun Vec3d.component1() =
	x

inline operator fun Vec3d.component2() =
	y

inline operator fun Vec3d.component3() =
	z
