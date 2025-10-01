package org.ender_development.catalyx.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.IItemProvider

abstract class AbstractItem(val settings: CatalyxSettings): Item(), IItemProvider {
	override val instance = this

	override val isEnabled = true

	override var modDependencies = ""

	init {
		creativeTab = settings.creativeTab
	}

	override fun requires(modDependencies: String): Item {
		this.modDependencies = modDependencies
		settings.items(this)
		return this
	}

	override fun register(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		ModelLoader.setCustomModelResourceLocation(instance, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
