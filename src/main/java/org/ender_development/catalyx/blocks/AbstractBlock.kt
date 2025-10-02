package org.ender_development.catalyx.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.CatalyxSettings
import org.ender_development.catalyx.core.IBlockProvider

abstract class AbstractBlock(val settings: CatalyxSettings, material: Material): Block(material), IBlockProvider {
	init {
		creativeTab = settings.creativeTab
	}

	override val instance = this

	override var modDependencies = ""

	override val item = ItemBlock(this)

	override val isEnabled = true

	override fun register(event: RegistryEvent.Register<Block>) =
		event.registry.register(this)

	override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
		item.registryName = registryName
		event.registry.register(item)
		ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(registryName!!, "inventory"))
	}

	override fun requires(modDependencies: String): Block {
		this.modDependencies = modDependencies
		settings.blocks(this)
		return this
	}
}
