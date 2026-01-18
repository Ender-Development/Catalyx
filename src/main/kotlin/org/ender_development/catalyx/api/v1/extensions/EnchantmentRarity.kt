package org.ender_development.catalyx.api.v1.extensions

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.EnumRarity

val Enchantment.Rarity.enumRarity: EnumRarity
	inline get() = EnumRarity.entries[ordinal]
