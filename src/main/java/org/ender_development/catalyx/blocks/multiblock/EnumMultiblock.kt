package org.ender_development.catalyx.blocks.multiblock

import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.blocks.multiblock.Facing.Companion.binary

enum class Position(val binary: Int) {
	P0(0b00),
	P1(0b01),
	P2(0b10),
	P3(0b11);
}

enum class Facing(val binary: Int, val facing: EnumFacing) {
	NORTH(0b00, EnumFacing.NORTH),
	EAST(0b01, EnumFacing.EAST),
	SOUTH(0b10, EnumFacing.SOUTH),
	WEST(0b11, EnumFacing.WEST);

	companion object {
		@Suppress("NOTHING_TO_INLINE")
		inline fun fromBinary(binary: Int) =
			Facing.entries[binary.coerceIn(0b00, 0b11)]

		internal val EnumFacing.binary: Int
			inline get() = Facing.entries.first { it.facing === this }.binary
	}
}

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun Facing.with(position: Position) =
	binary shl 2 or position.binary

@Suppress("NOTHING_TO_INLINE")
internal inline infix fun EnumFacing.with(position: Position) =
	binary shl 2 or position.binary
