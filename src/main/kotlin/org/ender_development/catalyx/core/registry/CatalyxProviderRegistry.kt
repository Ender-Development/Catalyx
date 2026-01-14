package org.ender_development.catalyx.core.registry

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.registry.ICatalyxProviderRegistry
import org.ender_development.catalyx.api.v1.registry.IProvider
import org.ender_development.catalyx.core.utils.extensions.modLoaded

/**
 * Collection of all [IProvider] of a given type
 *
 * @param V the Type of the Provider for the things that should be managed here
 */
class CatalyxProviderRegistry<V : IProvider<*>> :
	HashMap<ResourceLocation, Pair<V, Boolean>>(),
	ICatalyxProviderRegistry<V> {

	override fun add(provider: V): Boolean =
		provider.instance.registryName?.let {
			this[it] = provider to (provider.isEnabled() && provider.modDependencies.evaluateModDependencies())
			true
		} ?: false

	/**
	 * Special expansion to evaluate modDependencies strings.
	 *
	 * @return true if valid
	 * @see [IProvider.modDependencies]
	 */
	fun String.evaluateModDependencies(): Boolean =
		isEmpty() || split(',', ';').all {
			if(it[0] == '!')
				!it.substring(1).modLoaded()
			else
				it.modLoaded()
		}
}
