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
	 * Block array to automatically add all BaseBlocks to (for registration purposes) // TODO UPDATE JAVADOC
	 */
	val blocks: (IBlockProvider) -> Unit

	/**
	 * Item array to automatically add all BaseItems to (for registration purposes)
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

	// TODO javadoc
	fun modId(newModId: String) = CatalyxSettings(newModId, creativeTab, mod, enableItemCapability, blocks, items)
	fun creativeTab(newCreativeTab: CreativeTabs) = CatalyxSettings(modId, newCreativeTab, mod, enableItemCapability, blocks, items)
	fun mod(newMod: Any) = CatalyxSettings(modId, creativeTab, newMod, enableItemCapability, blocks, items)
	fun enableItemCapability(newEnableItemCapability: Boolean) = CatalyxSettings(modId, creativeTab, mod, newEnableItemCapability, blocks, items)
	fun blocks(newBlocks: (IBlockProvider) -> Unit) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, newBlocks, items)
	fun items(newItems: (IItemProvider) -> Unit) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, blocks, newItems)
}
