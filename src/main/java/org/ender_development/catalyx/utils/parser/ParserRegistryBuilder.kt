package org.ender_development.catalyx.utils.parser

import com.google.gson.reflect.TypeToken
import org.ender_development.catalyx.utils.validation.ValidationResult

class ParserRegistryBuilder {
	private val registry = ParserRegistry()

	internal inline fun <reified T> parser(key: String, parser: IParser<T>) = registry.registerParser(key, parser)

	internal inline fun <reified T> jsonParser(key: String, filePath: String, noinline defaultData: () -> List<T>, noinline sanitizer: (T) -> ValidationResult<T>) where T: Any {
		val parser = object : AbstractJsonParser<T, T>() {
			override fun getFilePath(): String = filePath

			override fun getDefaultRawData(): List<T> = defaultData()

			override fun sanitize(rawData: T): ValidationResult<T> = sanitizer(rawData)

			override fun getRawTypeToken(): TypeToken<List<T>> = object : TypeToken<List<T>>() {}
		}
		registry.registerParser(key, parser)
	}

	fun build(): IParserRegistry = registry
}

fun ParserRegistry(block: ParserRegistryBuilder.() -> Unit): ParserRegistry =
	ParserRegistryBuilder().apply(block).build() as ParserRegistry
