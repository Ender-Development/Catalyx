package org.ender_development.catalyx.core.utils.parser

class ParserRegistry : IParserRegistry {
	private val parsers = mutableMapOf<String, IParser<*>>()
	private val dataCache = mutableMapOf<String, List<*>>()
	private val lastRefresh = mutableMapOf<String, Long>()

	override fun <T> registerParser(key: String, parser: IParser<T>) {
		parsers[key] = parser
		dataCache.remove(key)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T> getParser(key: String): IParser<T>? =
		parsers[key] as? IParser<T>

	override fun <T> getData(key: String): List<T>? {
		if(dataCache.containsKey(key))
			@Suppress("UNCHECKED_CAST")
			return dataCache[key] as? List<T>

		val parser = getParser<T>(key) ?: return null
		val data = parser.parse()
		dataCache[key] = data
		lastRefresh[key] = System.currentTimeMillis()
		return data
	}

	override fun <T> search(key: String, predicate: (T) -> Boolean): List<T> {
		return (getData<T>(key) ?: return emptyList()).filter(predicate)
	}

	override val allKeys: Set<String>
		get() = parsers.keys

	override fun refreshAll(): Map<String, ParsingStats> {
		val results = mutableMapOf<String, ParsingStats>()
		parsers.keys.forEach { key ->
			refresh(key)?.let { results[key] = it }
		}
		return results
	}

	override fun refresh(key: String): ParsingStats? {
		val parser = parsers[key] ?: return null
		dataCache.remove(key)
		getData<Any>(key)
		return parser.stats
	}

	val cacheInfo: Map<String, CacheInfo>
		get() = parsers.keys.associateWith { CacheInfo(dataCache.containsKey(it), lastRefresh[it], dataCache[it]?.size ?: 0) }

	fun clearCache(key: String) {
		dataCache.remove(key)
		lastRefresh.remove(key)
	}

	fun clearCache() {
		dataCache.clear()
		lastRefresh.clear()
	}
}
