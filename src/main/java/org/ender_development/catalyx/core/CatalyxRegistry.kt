package org.ender_development.catalyx.core

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.DevUtils

@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxItemRegistry : IRegistry<IItemProvider, Item> {
	override val registry = ProviderHashSet<IItemProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Registry: ${registry.size} item${registry.plural}")
		registry.forEach {
			it.register(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered item: {}", it.item.registryName)
		}
	}
}

@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxBlockRegistry : IRegistry<IBlockProvider, Block> {
	override val registry = ProviderHashSet<IBlockProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Block>) {
		Catalyx.LOGGER.debug("Block Registry: ${registry.size} block${registry.plural}")
		registry.forEach { it.register(event) }
	}

	@SubscribeEvent
	fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Block Registry: ${registry.size} block item${registry.plural}")
		registry.forEach {
			it.registerItemBlock(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block item: {}", it.item.registryName)
		}
	}
}

class ProviderHashSet<K : IProvider<*>> : ObjectOpenHashSet<K>() {
	override fun add(k: K?) =
		k?.isEnabled == true && super.add(k)

	val plural: String
		get() = if(size == 1) "" else "s"
}
