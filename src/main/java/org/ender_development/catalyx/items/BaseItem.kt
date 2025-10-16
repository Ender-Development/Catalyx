package org.ender_development.catalyx.items

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.core.CatalyxSettings

/**
 * A base Catalyx item
 */
open class BaseItem(settings: CatalyxSettings, val name: String) : AbstractItem(settings) {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		settings.items(this)
	}
}
