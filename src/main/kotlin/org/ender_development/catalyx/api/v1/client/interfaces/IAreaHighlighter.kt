package org.ender_development.catalyx.api.v1.client.interfaces

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.ender_development.catalyx.api.v1.common.extensions.mapToArray

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

	/**
	 * Array of outlines that are currently being drawn
	 */
	val drawOutlinesFor: Array<Pair<Vec3d, Vec3d>>

	/**
	 * A BlockPos instance from the integer part of the starting coordinates for the first outline from [drawOutlinesFor]
	 *
	 * Returns [BlockPos.ORIGIN] (0, 0, 0) when not [drawing][shown] anything
	 */
	val pos1: BlockPos
		get() = BlockPos((drawOutlinesFor.getOrNull(0) ?: return BlockPos.ORIGIN).first)

	/**
	 * A BlockPos instance from the integer part of the ending coordinates for the first outline from [drawOutlinesFor]
	 *
	 * Returns [BlockPos.ORIGIN] (0, 0, 0) when not [drawing][shown] anything
	 */
	val pos2: BlockPos
		get() = BlockPos((drawOutlinesFor.getOrNull(0) ?: return BlockPos.ORIGIN).second)

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
		highlightAreas(arrayOf(pos.area()), r, g, b, time)

	/**
	 * Highlight an area between ([x1], [y1], [z1]) and ([x2], [y2], [z2]) with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightArea(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, r: Float, g: Float, b: Float, time: Int) =
		highlightAreas(arrayOf(Vec3d(x1, y1, z1) to Vec3d(x2, y2, z2)), r, g, b, time)

	/**
	 * Highlight the specified [blocks][blockPositions] with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightBlocks(blockPositions: Array<BlockPos>, r: Float, g: Float, b: Float, time: Int) =
		highlightAreas(blockPositions.mapToArray(BlockPos::area), r, g, b, time)

	/**
	 * Highlight the specified [areas] with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightAreas(areas: Array<Pair<Vec3d, Vec3d>>, r: Float, g: Float, b: Float, time: Int)

	/**
	 * Highlight the specified [areas] with a specific colour ([red][r], [green][g], [blue][b]) for a specified [time] in milliseconds
	 */
	fun highlightAreas(areas: Collection<Pair<Vec3d, Vec3d>>, r: Float, g: Float, b: Float, time: Int) =
		highlightAreas(areas.toTypedArray(), r, g, b, time)

	/**
	 * Stop this AreaHighlighter from rendering anything, does nothing when not [shown]
	 */
	fun hide()
}

private fun BlockPos.area(): Pair<Vec3d, Vec3d> =
	Vec3d(x.toDouble(), y.toDouble(), z.toDouble()) to Vec3d(x + 1.0, y + 1.0, z + 1.0)
