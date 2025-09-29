package org.ender_development.catalyx.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.IItemProvider

/**
 * A base Catalyx item
 */
open class BaseItem(settings: CatalyxSettings, val name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		creativeTab = settings.creativeTab
		settings.items(this)
	}

	override val item = this

	override val isEnabled = true

	override fun register(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
