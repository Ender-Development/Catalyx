package org.ender_development.catalyx.core.registry

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.plural
import org.ender_development.catalyx.api.v1.registry.IBlockProvider
import org.ender_development.catalyx.api.v1.registry.ICatalyxRegistry
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.client.ICustomModel

@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxBlockRegistry : ICatalyxRegistry<Block, IBlockProvider> {
	override val registry = CatalyxProviderRegistry<IBlockProvider>()

	@SubscribeEvent
	override fun registerProvider(event: RegistryEvent.Register<Block>) {
		Catalyx.LOGGER.debug("Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block${registry.size.plural}")
		registry.enabled.forEach {
			it.register(event)
			if(Utils.environment.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block: {}", it.instance.registryName)
		}
	}

	@SubscribeEvent
	fun registerItemBlocks(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Block Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} block item${registry.size.plural}")
		registry.enabled.forEach {
			it.registerItemBlock(event)
			if(Utils.environment.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered block item: {}", it.item.registryName)
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	override fun registerModel(event: ModelRegistryEvent) {
		registry.enabled.forEach {
			it.registerModel(event)
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	override fun bakeModel(event: ModelBakeEvent) {
		registry.enabled.filterIsInstance<ICustomModel>().forEach {
			it.onBakeModel(event)
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	override fun stitchTexture(event: TextureStitchEvent.Pre) {
		registry.enabled.filterIsInstance<ICustomModel>().forEach {
			it.onTextureStitch(event)
		}
	}
}
