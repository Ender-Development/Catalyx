package org.ender_development.catalyx.api.v1.common.extensions

inline fun <T, reified R> Array<T>.mapToArray(crossinline mapper: (T) -> R) =
	Array(size) { mapper(this[it]) }
