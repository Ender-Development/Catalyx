package org.ender_development.catalyx_.core.utils.validation

fun interface IValidator<T> {
	fun validate(value: T): Boolean
}
