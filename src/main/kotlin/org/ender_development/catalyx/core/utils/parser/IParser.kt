package org.ender_development.catalyx.core.utils.parser

interface IParser<T> {
	fun parse(): List<T>
	val input: String
	val stats: ParsingStats
}
