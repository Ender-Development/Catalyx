package org.ender_development.catalyx.core

import net.minecraft.creativetab.CreativeTabs
import org.ender_development.catalyx.core.registry.CatalyxBlockRegistry
import org.ender_development.catalyx.core.registry.CatalyxItemRegistry

/**
 * Helper class that contains mod-specific data to pass into Catalyx constructors
 */
class CatalyxSettings {
	/**
	 * Your mod's mod id
	 */
	val modId: String

	/**
	 * Your mod's creative tab
	 */
	val creativeTab: CreativeTabs

	/**
	 * Your mod's main class instance
	 */
	val mod: ICatalyxMod

	constructor(modId: String, creativeTab: CreativeTabs, mod: ICatalyxMod) {
		this.modId = modId
		this.creativeTab = creativeTab
		this.mod = mod
	}

	// helper functions
	@Suppress("NOTHING_TO_INLINE")
	inline fun register(block: IBlockProvider) =
		CatalyxBlockRegistry.registry.add(block)

	@Suppress("NOTHING_TO_INLINE")
	inline fun register(item: IItemProvider) =
		CatalyxItemRegistry.registry.add(item)
}
