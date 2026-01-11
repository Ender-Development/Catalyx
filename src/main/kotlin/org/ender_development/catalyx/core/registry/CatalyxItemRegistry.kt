package org.ender_development.catalyx.core.registry

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.core.IItemProvider
import org.ender_development.catalyx.utils.DevUtils

@Suppress("unused")
@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxItemRegistry : IRegistry<Item, IItemProvider> {
	override val registry = CatalyxRegister<IItemProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} item${registry.plural}")
		registry.enabled.forEach {
			it.register(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered item: {}", it.instance.registryName)
		}
	}
}
