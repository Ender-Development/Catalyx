package org.ender_development.catalyx.core

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
	 * The mod ID(s) required for this provider to be enabled, separated by semicolons. Prefixes of "!" can be used to indicate that a mod must NOT be present.
	 *
	 * Default is an empty string, meaning no dependencies.
	 */
	var modDependencies: String

	/**
	 * Whether this provider is enabled and should be registered.
	 */
	val isEnabled: Boolean

	/**
	 * Register this provider's item/block with the given event.
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<T>)

	/**
	 * Specify that this provider requires the given mod dependency to be present.
	 *
	 * @param modDependencies The mod ID(s) required, separated by semicolons. Prefixes of "!" can be used to indicate that a mod must NOT be present.
	 * @return This instance, for chaining.
	 */
	fun requires(modDependencies: String): T
}

interface IItemProvider : IProvider<Item>

interface IBlockProvider : IProvider<Block> {
	/**
	 * Override this instead of [registerItemBlock] if you only want to change the registered Item associated with this Block (like with a [org.ender_development.catalyx.items.TooltipItemBlock])
	 */
	val item: Item

	/**
	 * Register the Item for this Block with the given event.
	 *
	 * @param event The registry event for Items.
	 */
	fun registerItemBlock(event: RegistryEvent.Register<Item>)
}
