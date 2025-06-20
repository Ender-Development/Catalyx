package io.enderdev.catalyx

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

	constructor(modId: String, creativeTab: CreativeTabs, mod: Any, enableItemCapability: Boolean) {
		this.modId = modId
		this.creativeTab = creativeTab
		this.mod = mod
		this.enableItemCapability = enableItemCapability
	}
}
