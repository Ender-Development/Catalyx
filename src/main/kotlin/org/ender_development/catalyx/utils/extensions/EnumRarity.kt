package org.ender_development.catalyx.utils.extensions

import net.minecraft.item.EnumRarity
import org.ender_development.catalyx.utils.ColorMapping

val EnumRarity.colorValue
	inline get() = ColorMapping[this]

val EnumRarity.colorObject
	inline get() = ColorMapping.color(this)
