package org.ender_development.catalyx.core

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry

interface IRegistry<P : IProvider<E>, E> where E : IForgeRegistryEntry<E> {
	/**
	 * The set of providers to be registered.
	 */
	val registry: ObjectOpenHashSet<P>

	/**
	 * Register all enabled providers with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<E>)
}
