package org.ender_development.catalyx.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.IBlockProvider

/**
 * A base Catalyx Block
 */
open class BaseBlock(settings: CatalyxSettings, name: String, material: Material = Material.ROCK) : Block(material), IBlockProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		blockHardness = 3f
		creativeTab = settings.creativeTab
		settings.blocks(this)
	}

	override fun register(event: RegistryEvent.Register<Block>) =
		event.registry.register(this)

	/**
	 * Override this instead of [registerItemBlock] if you only want to change the registered Item associated with this Block (like with a [org.ender_development.catalyx.items.TooltipItemBlock])
	 */
	override val item: Item =
		ItemBlock(this)

	/**
	 * Whether or not to register this block
	 */
	override val isEnabled: Boolean = true

	override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
		item.registryName = registryName
		event.registry.register(item)
		ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
