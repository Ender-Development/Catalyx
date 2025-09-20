package org.ender_development.catalyx.blocks

import org.ender_development.catalyx.CatalyxSettings
import org.ender_development.catalyx.IBothProvider
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent

/**
 * A base Catalyx Block
 */
open class BaseBlock(settings: CatalyxSettings, name: String, material: Material = Material.ROCK) : Block(material), IBothProvider {
	init {
		registryName = ResourceLocation(settings.modId, name)
		translationKey = "$registryName"
		blockHardness = 3f
		creativeTab = settings.creativeTab
		settings.blocks(this)
	}

	/**
	 * You need to call this yourself, like
	 * ```kt
	 * catalyxSettings.blocks.forEach { it.registerBlocks(event) }
	 * ```
	 */
	override fun registerBlock(event: RegistryEvent.Register<Block>) =
		event.registry.register(this)

	/**
	 * Override this instead of `registerItem` if you only want to change the registered Item associated with this Block (like with a TooltipItemBlock)
	 */
	open fun createItemBlock(): Item =
		ItemBlock(this)

	/**
	 * You need to call this yourself, like
	 * ```kt
	 * catalyxSettings.items.forEach { it.registerItem(event) }
	 * ```
	 */
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		val item = createItemBlock().setRegistryName(registryName)
		event.registry.register(item)
		ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(registryName!!, "inventory"))
	}

	//@SideOnly(Side.CLIENT)
	//open fun registerModel() {
	//	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
	//}
}
