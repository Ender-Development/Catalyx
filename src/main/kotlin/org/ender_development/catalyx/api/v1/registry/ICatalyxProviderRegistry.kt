package org.ender_development.catalyx.api.v1.registry

import net.minecraft.util.ResourceLocation

interface ICatalyxProviderRegistry<V: IProvider<*>> : Map<ResourceLocation, Pair<V, Boolean>> {

	/**
	 * Adds a provider to this collection
	 *
	 * @return true if success
	 */
	fun add(provider: V): Boolean

	/**
	 * List of all enabled providers
	 */
	val enabled: List<V>
		get() = values.filter { it.second }.map { it.first }

	/**
	 * The lingual suffix of the plural
	 */
	val plural: String
		get() = if(enabled.size == 1) "" else "s" // WTF
}
