package org.ender_development.catalyx.utils.extensions

import com.google.common.collect.ImmutableSet

fun <T> Set<T>.toImmutableSet(): ImmutableSet<T> =
	ImmutableSet.copyOf(this)

fun <T> Set<T>.toSingletonSet(): Set<T> =
	ImmutableSet.copyOf(this.take(1))
