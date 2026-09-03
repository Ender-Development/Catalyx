package org.ender_development.catalyx.api.v1.registry

import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistryEntry
import org.ender_development.catalyx.core.common.registry.CatalyxProviderRegistry

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
	fun registerProvider(event: RegistryEvent.Register<E>)

	@SideOnly(Side.CLIENT)
	fun registerModel(event: ModelRegistryEvent)

	@SideOnly(Side.CLIENT)
	fun bakeModel(event: ModelBakeEvent)

	@SideOnly(Side.CLIENT)
	fun stitchTexture(event: TextureStitchEvent.Pre)
}
