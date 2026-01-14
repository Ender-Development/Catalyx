package org.ender_development.catalyx.api.v1.registry

import net.minecraft.util.ResourceLocation

interface ICatalyxProviderRegistry<V : IProvider<*>> : Map<ResourceLocation, Pair<V, Boolean>> {
	/**
	 * Adds a provider to this collection
	 *
	 * @return true, unless something stupid happened
	 */
	fun add(provider: V): Boolean

	/**
	 * List of all enabled providers
	 */
	val enabled: List<V>
		get() = values.mapNotNull { (provider, enabled) -> provider.takeIf { enabled } }
}
