package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.item.EnumDyeColor
import org.ender_development.catalyx.core.utils.ColorMapping

val EnumDyeColor.colorValue
	inline get() = ColorMapping[this]

val EnumDyeColor.colorObject
	inline get() = ColorMapping.color(this)
