package org.ender_development.catalyx_.core.utils.extensions

import it.unimi.dsi.fastutil.objects.ObjectSets
import java.util.*

fun <T> Set<T>.toImmutableSet(): Set<T> =
	Collections.unmodifiableSet(this)

fun <T> Set<T>.toSingletonSet(): Set<T> =
	ObjectSets.singleton(first())
