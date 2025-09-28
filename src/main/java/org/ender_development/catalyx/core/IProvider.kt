package org.ender_development.catalyx.core

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * A provider of items or blocks to be registered.
 */
interface IProvider<T> where T : IForgeRegistryEntry<T> {
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
}

interface IItemProvider : IProvider<Item> {
	/**
	 * The Item provided by this provider.
	 */
	val item: Item
}

interface IBlockProvider : IProvider<Block> {
	/**
	 * The Item associated with this Block
	 */
	val item: Item

	/**
	 * Register the Item for this Block with the given event.
	 *
	 * @param event The registry event for Items.
	 */
	fun registerItemBlock(event: RegistryEvent.Register<Item>)
}
