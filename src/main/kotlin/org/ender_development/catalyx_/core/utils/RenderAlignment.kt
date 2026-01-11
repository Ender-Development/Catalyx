@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx_.core.utils

class RenderAlignment(val vertical: Vertical, val horizontal: Horizontal) {
	enum class Vertical {
		TOP, MIDDLE, BOTTOM;
	}

	enum class Horizontal {
		LEFT, MIDDLE, RIGHT;
	}

	enum class Alignment {
		TOP_LEFT,    TOP_MIDDLE,    TOP_RIGHT,
		MIDDLE_LEFT, MIDDLE_MIDDLE, MIDDLE_RIGHT,
		BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT;

		// only reason these are not just properties like `enum class Alignment(val vertical: Vertical, val horizontal: Horizontal)` is because it's prettier
		val vertical: Vertical
			get() = when(this) {
				TOP_LEFT, TOP_MIDDLE, TOP_RIGHT -> Vertical.TOP
				MIDDLE_LEFT, MIDDLE_MIDDLE, MIDDLE_RIGHT -> Vertical.MIDDLE
				BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT -> Vertical.BOTTOM
			}

		val horizontal: Horizontal
			get() = when(this) {
				TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> Horizontal.LEFT
				TOP_MIDDLE, MIDDLE_MIDDLE, BOTTOM_MIDDLE -> Horizontal.MIDDLE
				TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> Horizontal.RIGHT
			}
	}

	constructor(alignment: Alignment) : this(alignment.vertical, alignment.horizontal)

	fun getX(left: Int, right: Int, width: Int, leftOffset: Int, rightOffset: Int) =
		when(horizontal) {
			Horizontal.LEFT -> left + leftOffset
			Horizontal.MIDDLE -> (left + right - width) shr 1
			Horizontal.RIGHT -> right - width - rightOffset
		}

	inline fun getX(left: Int, right: Int, width: Int, sideOffset: Int) =
		getX(left, right, width, sideOffset, sideOffset)

	fun getY(top: Int, bottom: Int, height: Int, topOffset: Int, bottomOffset: Int) =
		when(vertical) {
			Vertical.TOP -> top + topOffset
			Vertical.MIDDLE -> (top + bottom - height) shr 1
			Vertical.BOTTOM -> bottom - height - bottomOffset
		}

	inline fun getY(top: Int, bottom: Int, height: Int, sideOffset: Int) =
		getY(top, bottom, height, sideOffset, sideOffset)

	inline fun getXY(left: Int, right: Int, top: Int, bottom: Int, width: Int, height: Int, leftOffset: Int, rightOffset: Int, topOffset: Int, bottomOffset: Int) =
		getX(left, right, width, leftOffset, rightOffset) to getY(top, bottom, height, topOffset, bottomOffset)

	inline fun getXY(left: Int, right: Int, top: Int, bottom: Int, width: Int, height: Int, leftRightOffset: Int, topBottomOffset: Int) =
		getX(left, right, width, leftRightOffset) to getY(top, bottom, height, topBottomOffset)
}
