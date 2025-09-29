package org.ender_development.catalyx.core

import net.minecraft.creativetab.CreativeTabs

private typealias Blocks = (block: IBlockProvider) -> Unit
private typealias Items = (item: IItemProvider) -> Unit

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

	/**
	 * Whether to enable giving CapabilityItemHandler.ITEM_HANDLER_CAPABILITY in TileEntities
	 */
	val enableItemCapability: Boolean

	/**
	 * A function that takes in every instantiated block (meant for adding to a main `blocks` array for easier registration)), note: this is not required anymore as Catalyx can handle registration for you
	 */
	val blocks: Blocks

	/**
	 * A function that takes in every instantiated item (meant for adding to a main `items` array for easier registration), note: this is not required anymore as Catalyx can handle registration for you
	 */
	val items: Items

	constructor(modId: String, creativeTab: CreativeTabs, mod: ICatalyxMod, enableItemCapability: Boolean, blocks: Blocks = { CatalyxBlockRegistry.registry.add(it) }, items: Items = { CatalyxItemRegistry.registry.add(it) }) {
		this.modId = modId
		this.creativeTab = creativeTab
		this.mod = mod
		this.enableItemCapability = enableItemCapability
		this.blocks = blocks
		this.items = items
	}

	/** Create a new CatalyxSettings instance with a different mod id */
	fun modId(newModId: String) =
		CatalyxSettings(newModId, creativeTab, mod, enableItemCapability, blocks, items)

	/** Create a new CatalyxSettings instance with a different creative tab */
	fun creativeTab(newCreativeTab: CreativeTabs) =
		CatalyxSettings(modId, newCreativeTab, mod, enableItemCapability, blocks, items)

	/** Create a new CatalyxSettings instance with a different mod */
	fun mod(newMod: ICatalyxMod) =
		CatalyxSettings(modId, creativeTab, newMod, enableItemCapability, blocks, items)

	/** Create a new CatalyxSettings instance with a different item capability setting */
	fun enableItemCapability(newEnableItemCapability: Boolean) =
		CatalyxSettings(modId, creativeTab, mod, newEnableItemCapability, blocks, items)

	/** Create a new CatalyxSettings instance with a different block function */
	fun blocks(newBlocks: Blocks) =
		CatalyxSettings(modId, creativeTab, mod, enableItemCapability, newBlocks, items)

	/** Create a new CatalyxSettings instance with a different item function */
	fun items(newItems: Items) =
		CatalyxSettings(modId, creativeTab, mod, enableItemCapability, blocks, newItems)
}
