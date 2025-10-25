package org.ender_development.catalyx.utils.parser

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

	@Suppress("UNCHECKED_CAST")
	override fun <T> getData(key: String): List<T>? {
		if(dataCache.containsKey(key))
			return dataCache[key] as List<T>

		val parser = getParser<T>(key) ?: return null
		val data = parser.parse()
		dataCache[key] = data
		lastRefresh[key] = System.currentTimeMillis()
		return data
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T> search(key: String, predicate: (T) -> Boolean): List<T> {
		val data = getData<T>(key) ?: return emptyList()
		return data.filter(predicate)
	}

	override fun getAllKeys(): Set<String> =
		parsers.keys.toSet()

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
		return parser.getStats()
	}

	fun getCacheInfo(): Map<String, CacheInfo> =
		parsers.keys.associateWith { CacheInfo(dataCache.containsKey(it), lastRefresh[it], dataCache[it]?.size ?: 0) }

	fun clearCache(key: String? = null) {
		key?.let {
			dataCache.remove(it)
			lastRefresh.remove(it)
		} ?: run {
			dataCache.clear()
			lastRefresh.clear()
		}
	}
}
