package org.ender_development.catalyx.api.v1.registry

import net.minecraft.block.Block
import net.minecraft.item.Item
import org.ender_development.catalyx.core.registry.CatalyxBlockRegistry
import org.ender_development.catalyx.core.registry.CatalyxItemRegistry
import org.ender_development.catalyx.core.registry.CatalyxProviderRegistry

/**
 * API-Status: NOT-FROZEN
 * Beware
 */


object Registry {
	/**
	 * Factory for a new [CatalyxProviderRegistry]
	 */
	fun <V : IProvider<*>> newCatalyxProviderRegistry(): ICatalyxProviderRegistry<V> =
		CatalyxProviderRegistry()

	/**
	 * Contains the Catalyx implementation of the [ICatalyxRegistry] for [Item]
	 */
	val catalyxItemRegistry: ICatalyxRegistry<Item, IItemProvider> = CatalyxItemRegistry

	/**
	 * Contains the Catalyx implementation of the [ICatalyxRegistry] for [Block]
	 */
	val catalyxBlockRegistry: ICatalyxRegistry<Block, IBlockProvider> = CatalyxBlockRegistry
}
