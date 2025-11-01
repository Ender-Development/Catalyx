@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import java.awt.Color

inline fun Color.withAlpha(alpha: Float) =
	withAlpha((alpha * 255.0f).toInt())

inline fun Color.withAlpha(alpha: Int) =
	Color(red, green, blue, alpha)
