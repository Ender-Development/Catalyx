package org.ender_development.catalyx.api.v1.registry

import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry
import org.ender_development.catalyx.core.registry.CatalyxProviderRegistry

/**
 * A hook for a [CatalyxProviderRegistry] that lets register their content
 *
 * @param E the corresponding [IForgeRegistryEntry]
 * @param P the [IProvider] type
 */
interface ICatalyxRegistry<E : IForgeRegistryEntry<E>, P : IProvider<E>> {
	/**
	 * The set of providers to be registered.
	 */
	val registry: CatalyxProviderRegistry<P>

	/**
	 * Register all enabled providers with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<E>)
}
