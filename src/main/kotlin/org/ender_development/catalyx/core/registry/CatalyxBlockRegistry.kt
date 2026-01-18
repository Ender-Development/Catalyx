package org.ender_development.catalyx.core.registry

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.registry.IBlockProvider
import org.ender_development.catalyx.api.v1.registry.ICatalyxRegistry
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.utils.DevUtils
import org.ender_development.catalyx.core.utils.extensions.plural

@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxBlockRegistry : ICatalyxRegistry<Block, IBlockProvider> {
	override val registry = CatalyxProviderRegistry<IBlockProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Block>) {
		Catalyx.LOGGER.debug("Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block${registry.size.plural}")
		registry.enabled.forEach {
			it.register(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block: {}", it.instance.registryName)
		}
	}

	@SubscribeEvent
	fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block item${registry.size.plural}")
		registry.enabled.forEach {
			it.registerItemBlock(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block item: {}", it.item.registryName)
		}
	}
}
