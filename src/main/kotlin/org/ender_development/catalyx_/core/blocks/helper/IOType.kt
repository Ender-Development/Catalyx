package org.ender_development.catalyx_.core.blocks.helper

import net.minecraft.util.IStringSerializable

enum class IOType() : IStringSerializable {
	DEFAULT, NONE, INPUT, PULL, OUTPUT, PUSH;

	override fun getName() =
		name

	val next: IOType
		get() = entries[(ordinal + 1) % entries.size]

	val random: IOType
		inline get() = entries.random()
}
