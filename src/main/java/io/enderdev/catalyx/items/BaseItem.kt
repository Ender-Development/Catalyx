package io.enderdev.catalyx.items

import io.enderdev.catalyx.CatalyxRegistry
import io.enderdev.catalyx.CatalyxSettings
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent

/**
 * A base Catalyx item
 */
open class BaseItem(settings: CatalyxSettings, name: String) : Item() {
	init {
		CatalyxRegistry.items.add(this)
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		creativeTab = settings.creativeTab
	}

	/**
	 * Called within Catalyx
	 */
	open fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
	}

	//@SideOnly(Side.CLIENT)
	//open fun registerModel() {
	//	ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	//}
}
