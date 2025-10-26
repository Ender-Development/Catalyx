package org.ender_development.catalyx.utils.parser

interface IParserRegistry {
	fun <T> registerParser(key: String, parser: IParser<T>)
	fun <T> getParser(key: String): IParser<T>?
	fun <T> getData(key: String): List<T>?
	fun <T> search(key: String, predicate: (T) -> Boolean): List<T>
	val allKeys: Set<String>
	fun refreshAll(): Map<String, ParsingStats>
	fun refresh(key: String): ParsingStats?
}

data class CacheInfo(
	val isCached: Boolean,
	val lastRefresh: Long?,
	val itemCount: Int
)
