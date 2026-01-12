package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.EnumRarity
import org.ender_development.catalyx.core.utils.ColorMapping

val EnumRarity.colorValue
	inline get() = ColorMapping[this]

val EnumRarity.colorObject
	inline get() = ColorMapping.color(this)

val EnumRarity.enchantmentRarity: Enchantment.Rarity
	inline get() = Enchantment.Rarity.entries[ordinal]
