package org.ender_development.catalyx_.modules.coremodule.registry

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx_.modules.coremodule.IBlockProvider
import org.ender_development.catalyx_.core.utils.DevUtils

@Suppress("unused")
@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxBlockRegistry : IRegistry<Block, IBlockProvider> {
	override val registry = CatalyxRegister<IBlockProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Block>) {
		Catalyx.LOGGER.debug("Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block${registry.plural}")
		registry.enabled.forEach {
			it.register(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block: {}", it.instance.registryName)
		}
	}

	@SubscribeEvent
	fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block item${registry.plural}")
		registry.enabled.forEach {
			it.registerItemBlock(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block item: {}", it.item.registryName)
		}
	}
}
