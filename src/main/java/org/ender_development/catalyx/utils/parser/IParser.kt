package org.ender_development.catalyx.utils.parser

interface IParser<T> {
	fun parse(): List<T>
	val filePath: String
	val stats: ParsingStats
}
