package org.ender_development.catalyx_.core.utils.parser

import com.google.gson.reflect.TypeToken
import org.ender_development.catalyx_.core.utils.validation.ValidationResult

class ParserRegistryBuilder {
	private val registry = ParserRegistry()

	fun <T> parser(key: String, parser: IParser<T>) =
		registry.registerParser(key, parser)

	fun <T : Any> jsonParser(key: String, filePath: String, defaultData: () -> List<T>, sanitizer: (T) -> ValidationResult<T>) {
		val parser = object : AbstractJsonParser<T, T>() {
			override val filePath = filePath

			override val defaultRawData: List<T>
				get() = defaultData()

			override val rawTypeToken = object : TypeToken<List<T>>() {}

			override fun sanitize(rawData: T): ValidationResult<T> =
				sanitizer(rawData)
		}
		registry.registerParser(key, parser)
	}

	fun build(): IParserRegistry =
		registry
}

fun ParserRegistry(block: ParserRegistryBuilder.() -> Unit): ParserRegistry =
	ParserRegistryBuilder().apply(block).build() as ParserRegistry
