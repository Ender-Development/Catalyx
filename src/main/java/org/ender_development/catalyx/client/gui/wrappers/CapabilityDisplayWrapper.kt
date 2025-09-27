package org.ender_development.catalyx.client.gui.wrappers

import java.text.NumberFormat
import java.util.Locale

abstract class CapabilityDisplayWrapper(val x: Int, val y: Int, val width: Int, val height: Int) {
	abstract val stored: Int
	abstract val capacity: Int
	abstract val textLines: List<String>

	companion object {
		val numFormat: NumberFormat = NumberFormat.getInstance(Locale.getDefault())
	}
}
