package io.enderdev.catalyx.blocks

import io.enderdev.catalyx.CatalyxRegistry
import io.enderdev.catalyx.CatalyxSettings
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent

/**
 * A base Catalyx Block
 */
open class BaseBlock(settings: CatalyxSettings, name: String, material: Material = Material.ROCK) : Block(material) {
	init {
		CatalyxRegistry.blocks.add(this)
		translationKey = name
		registryName = ResourceLocation(settings.modId, name)
		blockHardness = 3f
		creativeTab = settings.creativeTab
	}

	/**
	 * Called within Catalyx
	 */
	open fun registerBlock(event: RegistryEvent.Register<Block>) {
		event.registry.register(this)
	}

	/**
	 * Called within Catalyx
	 */
	open fun registerItemBlock(event: RegistryEvent.Register<Item>) {
		event.registry.register(ItemBlock(this).setRegistryName(registryName))
	}

	//@SideOnly(Side.CLIENT)
	//open fun registerModel() {
	//	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
	//}
}
