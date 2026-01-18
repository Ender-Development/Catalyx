package org.ender_development.catalyx.api.v1.registry

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * A provider of items or blocks to be registered.
 */
interface IProvider<T : IForgeRegistryEntry<T>> {
	/**
	 * The instance provided by this provider.
	 */
	val instance: T

	/**
	 * The mod ID(s) required for this provider to be enabled. Prefixes of "!" can be used to indicate that a mod must NOT be present.
	 *
	 * Default is an empty list, meaning no dependencies.
	 *
	 * @see [org.ender_development.catalyx.core.registry.CatalyxProviderRegistry.evaluateModDependencies]
	 */
	val modDependencies: Iterable<String>

	// Note: do not make this a `val` as we wanna enforce a getter here
	/**
	 * Whether this provider is enabled and should be registered.
	 */
	fun isEnabled(): Boolean

	/**
	 * Register this provider's item/block with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<T>)

	/**
	 * Specify that this provider requires the given mod dependencies to be present.
	 *
	 * @see IProvider.modDependencies for format
	 */
	fun requires(modDependencies: Iterable<String>): T
}

interface IItemProvider : IProvider<Item>

interface IBlockProvider : IProvider<Block> {
	/**
	 * Override this instead of [registerItemBlock] if you only want to change the registered Item associated with this Block (like with a [org.ender_development.catalyx.core.items.TooltipItemBlock])
	 */
	val item: Item

	/**
	 * Register the Item for this Block with the given event.
	 *
	 * @param event The registry event for Items.
	 */
	fun registerItemBlock(event: RegistryEvent.Register<Item>)
}
