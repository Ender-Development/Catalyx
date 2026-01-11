package org.ender_development.catalyx_.core.utils.extensions

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.EnumRarity

val Enchantment.Rarity.enumRarity: EnumRarity
	inline get() = EnumRarity.entries[ordinal]
