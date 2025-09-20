package org.ender_development.catalyx.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import org.ender_development.catalyx.CatalyxSettings
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.items.IItemProvider

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

	/**
	 * You need to call this yourself, like
	 * ```kt
	 * YourModItems.items.forEach { it.registerItem(event) }
	 * ```
	 */
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
