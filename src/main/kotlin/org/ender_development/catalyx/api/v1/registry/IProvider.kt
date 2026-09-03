package org.ender_development.catalyx.api.v1.registry

import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistryEntry

/**
 * A provider of items or blocks to be registered.
 */
interface IProvider<T : IForgeRegistryEntry<T>> {
	/**
	 * The instance provided by this provider.
	 */
	val instance: T

	/**
	 * Whether this provider is enabled and should be registered.
	 */
	fun isEnabled() =
		true

	/**
	 * Register this provider's item/block with the given event.
	 * This will only be called when the provider is [enabled][isEnabled].
	 *
	 * @param event The registry event.
	 */
	fun register(event: RegistryEvent.Register<T>) =
		event.registry.register(instance)

	/**
	 * Register this provider's model, this comes with a default implementation.
	 *
	 * @param event The registry event.
	 */
	@SideOnly(Side.CLIENT)
	fun registerModel(event: ModelRegistryEvent)

	/**
	 * [ResourceLocation] of the model parent
	 */
	val modelParent: ResourceLocation

	/**
	 * [ResourceLocation] of the model file
	 */
	val modelLocation: ResourceLocation

	/**
	 * [ResourceLocation] of the texture file
	 */
	val textureLocation: ResourceLocation
}

interface IItemProvider : IProvider<Item> {
	@SideOnly(Side.CLIENT)
	override fun registerModel(event: ModelRegistryEvent) =
		ModelLoader.setCustomModelResourceLocation(instance, 0, ModelResourceLocation(instance.registryName!!, "inventory"))

	override val modelParent: ResourceLocation
		get() = ResourceLocation("minecraft", "item/generated")

	override val modelLocation: ResourceLocation
		get() = ResourceLocation(instance.registryName!!.namespace, "item/${instance.registryName!!.path}")

	override val textureLocation: ResourceLocation
		get() = ResourceLocation(instance.registryName!!.namespace, "items/${instance.registryName!!.path}")
}

interface IBlockProvider : IProvider<Block> {
	/**
	 * Override this instead of [registerItemBlock] if you only want to change the registered Item associated with this Block (like with a [org.ender_development.catalyx.core.common.items.TooltipItemBlock])
	 */
	val item: Item
		get() = ItemBlock(instance).setRegistryName(instance.registryName)

	/**
	 * Register the Item for this Block with the given event.
	 * This will only be called, when the provider is [enabled][isEnabled].
	 *
	 * @param event The registry event for Items.
	 */
	fun registerItemBlock(event: RegistryEvent.Register<Item>) =
		event.registry.register(item)

	@SideOnly(Side.CLIENT)
	override fun registerModel(event: ModelRegistryEvent) =
		ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName!!, "inventory"))

	override val textureLocation: ResourceLocation
		get() = ResourceLocation(instance.registryName!!.namespace, "blocks/${instance.registryName!!.path}")

	override val modelLocation: ResourceLocation
		get() = ResourceLocation(instance.registryName!!.namespace, "block/${instance.registryName!!.path}")

	override val modelParent: ResourceLocation
		get() = ResourceLocation("minecraft", "block/cube_all")
}
