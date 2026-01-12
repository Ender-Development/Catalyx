package org.ender_development.catalyx.core.tiles.helper

import net.minecraft.util.EnumFacing
import java.awt.Color

interface IHudInfoProvider {
	fun getHudInfo(face: EnumFacing): Array<HudInfoLine>
}

data class HudInfoLine(val text: String, val color: Color? = null, val background: Color? = null, val border: Color? = null) {
	var percent = 0f
	var percentColor: Color? = null
	var alignment = TextAlign.LEFT

	fun textAlign(align: TextAlign): HudInfoLine {
		alignment = align
		return this
	}

	fun progress(percent: Float, percentColor: Color): HudInfoLine {
		this.percent = percent
		this.percentColor = percentColor
		return this
	}

	enum class TextAlign {
		LEFT, CENTER, RIGHT
	}
}
