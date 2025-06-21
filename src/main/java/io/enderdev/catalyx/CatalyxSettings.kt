package io.enderdev.catalyx

import io.enderdev.catalyx.blocks.IBlockProvider
import io.enderdev.catalyx.items.IItemProvider
import net.minecraft.creativetab.CreativeTabs

/**
 * Helper class that contains mod-specific data to pass into Catalyx constructors
 */
class CatalyxSettings {
	/**
	 * Your mod's id
	 */
	val modId: String

	/**
	 * Your mod's creative tab
	 */
	val creativeTab: CreativeTabs

	/**
	 * Your mod's main class; example - new CatalyxSettings(Tags.MOD_ID, Alchemistry.creativeTab, Alchemistry, true)
	 */
	val mod: Any

	/**
	 * Whether to enable giving CapabilityItemHandler.ITEM_HANDLER_CAPABILITY in TileEntities
	 */
	val enableItemCapability: Boolean

	/**
	 * A function that will get called with every BaseBlock that gets instanced, meant for making it easier to add them to Minecraft's registry
	 */
	val blocks: (IBlockProvider) -> Unit

	/**
	 * A function that will get called with every BaseItem that gets instanced, meant for making it easier to add them to Minecraft's registry
	 */
	val items: (IItemProvider) -> Unit

	constructor(modId: String, creativeTab: CreativeTabs, mod: Any, enableItemCapability: Boolean, blocks: (IBlockProvider) -> Unit, items: (IItemProvider) -> Unit) {
		this.modId = modId
		this.creativeTab = creativeTab
		this.mod = mod
		this.enableItemCapability = enableItemCapability
		this.blocks = blocks
		this.items = items
	}

	/** Create a new CatalyxSettings instance with a different mod id */
	fun modId(newModId: String) = CatalyxSettings(newModId, creativeTab, mod, enableItemCapability, blocks, items)
	/** Create a new CatalyxSettings instance with a different creative tab */
	fun creativeTab(newCreativeTab: CreativeTabs) = CatalyxSettings(modId, newCreativeTab, mod, enableItemCapability, blocks, items)
	/** Create a new CatalyxSettings instance with a different mod */
	fun mod(newMod: Any) = CatalyxSettings(modId, creativeTab, newMod, enableItemCapability, blocks, items)
	/** Create a new CatalyxSettings instance with a different item capability setting */
	fun enableItemCapability(newEnableItemCapability: Boolean) = CatalyxSettings(modId, creativeTab, mod, newEnableItemCapability, blocks, items)
	/** Create a new CatalyxSettings instance with a different block function */
	fun blocks(newBlocks: (IBlockProvider) -> Unit) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, newBlocks, items)
	/** Create a new CatalyxSettings instance with a different item function */
	fun items(newItems: (IItemProvider) -> Unit) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, blocks, newItems)
}
