package io.enderdev.catalyx.blocks

import io.enderdev.catalyx.CatalyxSettings
import io.enderdev.catalyx.items.IItemProvider
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent

/**
 * A base Catalyx Block
 */
open class BaseBlock(settings: CatalyxSettings, name: String, material: Material = Material.ROCK) : Block(material), IBlockProvider, IItemProvider {
	init {
		translationKey = name
		registryName = ResourceLocation(settings.modId, name)
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
	override fun registerBlock(event: RegistryEvent.Register<Block>) {
		event.registry.register(this)
	}

	/**
	 * You need to call this yourself, like
	 * ```kt
	 * catalyxSettings.items.forEach { it.registerItem(event) }
	 * ```
	 */
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(ItemBlock(this).setRegistryName(registryName))
	}

	//@SideOnly(Side.CLIENT)
	//open fun registerModel() {
	//	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
	//}
}
