package org.ender_development.catalyx.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.IItemProvider
import org.ender_development.catalyx.core.register
import org.ender_development.catalyx.utils.SideUtils

/**
 * A base Catalyx item
 */
open class BaseItem(val mod: ICatalyxMod, val name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(mod.modId, name)
		translationKey = "$registryName"
		creativeTab = mod.creativeTab
		mod.register(this)
	}

	// this needs to be a getter for whatever reason, otherwise a very fun problem will affect dependent mods
	override val instance
		get() = this

	override val isEnabled = true

	override var modDependencies = ""

	override fun requires(modDependencies: String): Item {
		this.modDependencies = modDependencies
		mod.register(this)
		return this
	}

	override fun register(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		if(SideUtils.isClient)
			ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
