@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils.extensions

import java.awt.Color

inline fun Color.withAlpha(alpha: Float) =
	withAlpha((alpha * 255f).toInt())

inline fun Color.withAlpha(alpha: Int) =
	Color(red, green, blue, alpha)

inline operator fun Color.component1() =
	red

inline operator fun Color.component2() =
	green

inline operator fun Color.component3() =
	blue

inline operator fun Color.component4() =
	alpha

inline fun Color.destructFloat() =
	floatArrayOf(red / 255f, green / 255f, blue / 255f, alpha / 255f)
