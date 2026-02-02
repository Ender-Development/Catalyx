package org.ender_development.catalyx.core.items

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.registry.IItemProvider
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.register

/**
 * A base Catalyx item
 */
open class BaseItem(val mod: ICatalyxMod, val name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(mod.modId, name)
		translationKey = "${mod.modId}.$name"
		creativeTab = mod.creativeTab
	}

	override val instance = this

	init {
		mod.register(this)
	}
}
