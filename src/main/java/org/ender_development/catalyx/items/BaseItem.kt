package org.ender_development.catalyx.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.IItemProvider
import org.ender_development.catalyx.utils.SideUtils
import scala.reflect.macros.contexts.`Infrastructure$class`.settings

/**
 * A base Catalyx item
 */
open class BaseItem(val settings: CatalyxSettings, val name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		creativeTab = settings.creativeTab
		settings.register(this)
	}

	override val instance = this

	override val isEnabled = true

	override var modDependencies = ""

	override fun requires(modDependencies: String): Item {
		this.modDependencies = modDependencies
		settings.register(this)
		return this
	}

	override fun register(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		if(SideUtils.isClient)
			ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
