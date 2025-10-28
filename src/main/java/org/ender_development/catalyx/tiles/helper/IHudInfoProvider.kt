package org.ender_development.catalyx.tiles.helper

import net.minecraft.util.EnumFacing
import java.awt.Color

interface IHudInfoProvider {
	fun getHudInfo(face: EnumFacing?): List<HudInfoLine>
}

class HudInfoLine(val color: Color?, val background: Color?, val border: Color?, val text: String) {
	var percent = 0.0f
	var percentColor: Color? = null
	var alignment = TextAlign.LEFT

	constructor(text: String) : this(null, null, null, text)
	constructor(color: Color, text: String) : this(color, null, null, text)
	constructor(color: Color, background: Color, text: String) : this(color, background, null, text)

	fun setTextAlign(align: TextAlign): HudInfoLine {
		this.alignment = align
		return this
	}

	fun setProgress(percent: Float, percentColor: Color): HudInfoLine {
		this.percent = percent
		this.percentColor = percentColor
		return this
	}

	enum class TextAlign {
		LEFT, CENTER, RIGHT
	}
}
