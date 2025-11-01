package org.ender_development.catalyx.core.registry

import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry
import org.ender_development.catalyx.core.IProvider

interface IRegistry<E : IForgeRegistryEntry<E>, P : IProvider<E>> {
	/**
	 * The set of providers to be registered.
	 */
	val registry: CatalyxRegister<P>

	/**
	 * Register all enabled providers with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<E>)
}
