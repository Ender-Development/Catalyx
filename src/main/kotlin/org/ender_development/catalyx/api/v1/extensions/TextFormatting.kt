package org.ender_development.catalyx.api.v1.extensions

import net.minecraft.util.text.TextFormatting
import org.ender_development.catalyx.core.utils.ColorMapping

val TextFormatting.colorValue
	inline get() = ColorMapping[this]

val TextFormatting.colorObject
	inline get() = ColorMapping.color(this)
