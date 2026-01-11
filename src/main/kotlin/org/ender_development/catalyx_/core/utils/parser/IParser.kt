package org.ender_development.catalyx_.core.utils.parser

interface IParser<T> {
	fun parse(): List<T>
	val filePath: String
	val stats: ParsingStats
}
