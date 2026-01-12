@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils

import net.minecraft.item.EnumDyeColor
import net.minecraft.item.EnumRarity
import net.minecraft.util.text.TextFormatting
import java.awt.Color

/**
 * Mappings from [EnumDyeColor], [TextFormatting] and [EnumRarity] to their actual color values.
 */
object ColorMapping {
	/**
	 * Colors copied from [EnumDyeColor]
	 */
	object Dye {
		const val WHITE = 0xf9fffe
		const val ORANGE = 0xf9801d
		const val MAGENTA = 0xc74ebd
		const val LIGHT_BLUE = 0x3ab3da
		const val YELLOW = 0xfed83d
		const val LIME = 0x80c71f
		const val PINK = 0xf38baa
		const val GRAY = 0x474f52
		const val SILVER = 0x9d9d97
		const val CYAN = 0x169c9c
		const val PURPLE = 0x8932b8
		const val BLUE = 0x3c44aa
		const val BROWN = 0x835432
		const val GREEN = 0x5e7c16
		const val RED = 0xb02e26
		const val BLACK = 0x1d1d21

		val colors = intArrayOf(WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, SILVER, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK)
		val colorObjects = colors.map(::Color)

		inline operator fun get(dyeColor: EnumDyeColor) =
			colors[dyeColor.ordinal]

		inline fun color(dyeColor: EnumDyeColor) =
			colorObjects[dyeColor.ordinal]
	}

	/**
	 * Colors copied from [net.minecraft.client.gui.FontRenderer.colorCode]
	 */
	object Formatting {
		const val BLACK = 0x0
		const val DARK_BLUE = 0xaa
		const val DARK_GREEN = 0xaa00
		const val DARK_AQUA = 0xaaaa
		const val DARK_RED = 0xaa0000
		const val DARK_PURPLE = 0xaa00aa
		const val GOLD = 0xffaa00
		const val GRAY = 0xaaaaaa
		const val DARK_GRAY = 0x555555
		const val BLUE = 0x5555ff
		const val GREEN = 0x55ff55
		const val AQUA = 0x55ffff
		const val RED = 0xff5555
		const val LIGHT_PURPLE = 0xff55ff
		const val YELLOW = 0xffff55
		const val WHITE = 0xffffff
		const val OBFUSCATED = -1
		const val BOLD = -1
		const val STRIKETHROUGH = -1
		const val UNDERLINE = -1
		const val ITALIC = -1
		const val RESET = -1

		val colors = intArrayOf(BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET)
		val colorObjects = colors.map(::Color)

		inline operator fun get(textFormatting: TextFormatting) =
			colors[textFormatting.ordinal]

		inline fun color(textFormatting: TextFormatting) =
			colorObjects[textFormatting.ordinal]
	}

	/**
	 * [EnumRarity] uses [TextFormatting] afaict
	 */
	object Rarity {
		const val COMMON = Formatting.WHITE
		const val UNCOMMON = Formatting.YELLOW
		const val RARE = Formatting.AQUA
		const val EPIC = Formatting.LIGHT_PURPLE

		val colors = intArrayOf(COMMON, UNCOMMON, RARE, EPIC)
		val colorObjects = colors.map(::Color)

		inline operator fun get(rarity: EnumRarity) =
			colors[rarity.ordinal]

		inline fun color(rarity: EnumRarity) =
			colorObjects[rarity.ordinal]
	}

	inline operator fun get(dyeColor: EnumDyeColor) =
		Dye[dyeColor]

	inline operator fun get(textFormatting: TextFormatting) =
		Formatting[textFormatting]

	inline operator fun get(rarity: EnumRarity) =
		Rarity[rarity]

	inline fun color(dyeColor: EnumDyeColor) =
		Dye.color(dyeColor)

	inline fun color(textFormatting: TextFormatting) =
		Formatting.color(textFormatting)

	inline fun color(rarity: EnumRarity) =
		Rarity.color(rarity)
}
