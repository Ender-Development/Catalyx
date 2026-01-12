package org.ender_development.catalyx.core.registry

import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry

interface ICatalyxRegistry<E : IForgeRegistryEntry<E>, P : IProvider<E>> {
	/**
	 * The set of providers to be registered.
	 */
	val registry: CatalyxRegistry<P>

	/**
	 * Register all enabled providers with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<E>)
}
