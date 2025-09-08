package org.ender_development.catalyx.items

import org.ender_development.catalyx.CatalyxSettings
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent

/**
 * A base Catalyx item
 */
open class BaseItem(settings: CatalyxSettings, name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		creativeTab = settings.creativeTab
		settings.items(this)
	}

	/**
	 * You need to call this yourself, like
	 * ```kt
	 * catalyxSettings.items.forEach { it.registerItem(event) }
	 * ```
	 */
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
	}

	//@SideOnly(Side.CLIENT)
	//open fun registerModel() {
	//	ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	//}
}
