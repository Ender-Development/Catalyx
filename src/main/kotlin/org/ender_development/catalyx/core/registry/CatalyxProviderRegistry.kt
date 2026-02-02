package org.ender_development.catalyx.core.registry

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.common.extensions.modLoaded
import org.ender_development.catalyx.api.v1.registry.ICatalyxProviderRegistry
import org.ender_development.catalyx.api.v1.registry.IProvider
import kotlin.let

/**
 * Collection of all [IProvider] of a given type
 *
 * @param V the Type of the Provider for the things that should be managed here
 */
class CatalyxProviderRegistry<V : IProvider<*>> : HashMap<ResourceLocation, Pair<V, Boolean>>(), ICatalyxProviderRegistry<V> {
	override fun add(provider: V): Boolean =
		provider.instance.registryName?.let {
			this[it] = provider to provider.enabled()
			true
		} ?: false
}
