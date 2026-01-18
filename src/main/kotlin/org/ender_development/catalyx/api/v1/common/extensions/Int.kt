package org.ender_development.catalyx.api.v1.common.extensions

val Int.plural
	inline get() = if(this == 1) "" else "s"
