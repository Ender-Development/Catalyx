package io.enderdev.catalyx

import io.enderdev.catalyx.blocks.IBlockProvider
import io.enderdev.catalyx.items.IItemProvider
import net.minecraft.creativetab.CreativeTabs

interface IBothProvider : IItemProvider, IBlockProvider

internal typealias Blocks = (block: IBothProvider) -> Unit
internal typealias Items = (item: IItemProvider) -> Unit

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
	 * whatever the fuck this is, I've changed this field like 5 times by now
	 */
	val blocks: Blocks

	/**
	 * whatever the fuck this is, I've changed this field like 5 times by now
	 */
	val items: Items

	constructor(modId: String, creativeTab: CreativeTabs, mod: Any, enableItemCapability: Boolean, blocks: Blocks, items: Items) {
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
	fun blocks(newBlocks: Blocks) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, newBlocks, items)
	/** Create a new CatalyxSettings instance with a different item function */
	fun items(newItems: Items) = CatalyxSettings(modId, creativeTab, mod, enableItemCapability, blocks, newItems)
}
