package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.item.EnumDyeColor
import org.ender_development.catalyx.api.v1.common.ColorMapping

val EnumDyeColor.colorValue
	inline get() = ColorMapping[this]

val EnumDyeColor.colorObject
	inline get() = ColorMapping.color(this)
