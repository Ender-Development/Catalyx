package org.ender_development.catalyx.utils.validation

fun interface IValidator<T> {
	fun validate(value: T): Boolean
}
