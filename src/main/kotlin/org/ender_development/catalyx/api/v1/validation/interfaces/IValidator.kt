package org.ender_development.catalyx.api.v1.validation.interfaces

fun interface IValidator<T> {
	fun validate(value: T): Boolean
}
