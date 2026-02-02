package org.ender_development.catalyx.api.v1.client.interfaces

import net.minecraft.util.math.BlockPos

/**
 * A helper allowing you to highlight an area, block or blocks in 3D space
 * @see [highlightBlock]
 * @see [highlightBlocks]
 * @see [highlightArea]
 */
interface IAreaHighlighter {
	/**
	 * Whether this AreaHighlighter is actually rendering anything
	 */
	val shown: Boolean

	// Starting X, Y, Z
	val x1: Double
	val y1: Double
	val z1: Double
	/**
	 * A BlockPos instance from the integer part of the (starting) [x1] [y1] [z1] coordinates.
	 *
	 * N/A when [highlightBlocks] is used.
	 */
	val pos1
		get() = BlockPos(x1, y1, z1)

	// Ending X, Y, Z
	val x2: Double
	val y2: Double
	val z2: Double

	/**
	 * A BlockPos instance from the integer part of the (ending) [x2] [y2] [z2] coordinates.
	 *
	 * N/A when [highlightBlocks] is used.
	 */
	val pos2
		get() = BlockPos(x2, y2, z2)

	val drawBlockPositions: Boolean
	val drawnBlockPositions: Array<BlockPos>

	// R, G, B colour channels
	val r: Float
	val g: Float
	val b: Float

	/**
	 * Absolute time in milliseconds when this AreaHighlighter will automatically [hide]
	 */
	val until: Long

	/**
	 * Line thickness used in the renderer
	 */
	var thickness: Float

	/**
	 * Highlight a [block position][pos] with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightBlock(pos: BlockPos, r: Float, g: Float, b: Float, time: Int) =
		highlightArea(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pos.x + 1.0, pos.y + 1.0, pos.z + 1.0, r, g, b, time)

	/**
	 * Highlight an area between ([x1], [y1], [z1]) and ([x2], [y2], [z2]) with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightArea(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, r: Float, g: Float, b: Float, time: Int)

	/**
	 * Highlight the specified [blocks][blockPositions] with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightBlocks(blockPositions: Array<BlockPos>, r: Float, g: Float, b: Float, time: Int)

	/**
	 * Stop this AreaHighlighter from rendering anything, does nothing when not [shown]
	 */
	fun hide()
}
