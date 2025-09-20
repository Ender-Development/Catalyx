package org.ender_development.catalyx.recipes.maps

abstract class AbstractMapIngredient protected constructor() {
	private var hash = 0
	private var hashed = false

	protected abstract fun hash(): Int

	override fun hashCode(): Int {
		if(!hashed) {
			hash = hash()
			hashed = true
		}
		return hash
	}

	protected fun invalidate() {
		this.hashed = false
	}

	override fun equals(other: Any?) =
		this === other || (other != null && this::class.java === other::class.java)

	open val isSpecialIngredient = false
}
