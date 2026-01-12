package org.ender_development.catalyx.core.utils.parser

interface IParser<T> {
	fun parse(): List<T>
	val filePath: String
	val stats: ParsingStats
}
