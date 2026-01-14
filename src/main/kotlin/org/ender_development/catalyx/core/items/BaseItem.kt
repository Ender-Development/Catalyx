package org.ender_development.catalyx.core.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.api.v1.registry.IItemProvider
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.register
import org.ender_development.catalyx.core.utils.SideUtils

/**
 * A base Catalyx item
 */
open class BaseItem(val mod: ICatalyxMod, val name: String) : Item(), IItemProvider {
	init {
		registryName = ResourceLocation(mod.modId, name)
		translationKey = "$registryName"
		creativeTab = mod.creativeTab
	}

	override val instance = this

	override fun isEnabled() =
		true

	final override var modDependencies: Iterable<String> = emptyList()
		private set

	override fun requires(modDependencies: Iterable<String>): Item {
		this.modDependencies = modDependencies
		mod.register(this)
		return this
	}

	override fun register(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		if(SideUtils.isClient)
			ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}

	init {
		mod.register(this)
	}
}
