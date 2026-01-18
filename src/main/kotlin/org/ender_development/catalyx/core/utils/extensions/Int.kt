package org.ender_development.catalyx.core.utils.extensions

val Int.plural
	inline get() = if(this == 1) "" else "s"
