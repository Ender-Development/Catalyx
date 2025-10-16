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
import org.ender_development.catalyx.utils.SideUtils
import scala.reflect.macros.contexts.`Infrastructure$class`.settings

/**
 * A base Catalyx Block
 */
open class BaseBlock(val settings: CatalyxSettings, name: String, material: Material = Material.ROCK, hardness: Float = 3f) : Block(material), IBlockProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		blockHardness = hardness
		creativeTab = settings.creativeTab
		settings.register(this)
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
		if(SideUtils.isClient)
			ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(registryName!!, "inventory"))
	}

	override fun requires(modDependencies: String): Block {
		this.modDependencies = modDependencies
		settings.register(this)
		return this
	}
}
