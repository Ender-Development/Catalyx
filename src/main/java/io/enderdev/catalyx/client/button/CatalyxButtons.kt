package io.enderdev.catalyx.client.button

object CatalyxButtons {
	private var id = 0

	fun nextId(): Int {
		return id++
	}
}
