package org.ender_development.catalyx.utils.parser

interface IParser<T> {
	fun parse(): List<T>
	fun getFilePath(): String
	fun getStats(): ParsingStats
}
