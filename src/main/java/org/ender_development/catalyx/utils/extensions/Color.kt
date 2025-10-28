package org.ender_development.catalyx.utils.extensions

import java.awt.Color

inline fun Color.withAlpha(alpha: Float) = this.withAlpha((alpha * 255.0f + 0.5f).toInt())

inline fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
