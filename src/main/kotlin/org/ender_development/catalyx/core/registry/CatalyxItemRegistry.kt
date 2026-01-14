package org.ender_development.catalyx.core.registry

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.registry.ICatalyxRegistry
import org.ender_development.catalyx.api.v1.registry.IItemProvider
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.utils.DevUtils
import org.ender_development.catalyx.core.utils.extensions.plural

@Suppress("unused")
@Mod.EventBusSubscriber(modid = Reference.MODID)
object CatalyxItemRegistry : ICatalyxRegistry<Item, IItemProvider> {
	override val registry = CatalyxProviderRegistry<IItemProvider>()

	@SubscribeEvent
	override fun register(event: RegistryEvent.Register<Item>) {
		Catalyx.LOGGER.debug("Item Registry has ${registry.size} entries, but only gonna register ${registry.enabled.size} item${registry.size.plural}")
		registry.enabled.forEach {
			it.register(event)
			if(DevUtils.isDeobfuscated)
				Catalyx.LOGGER.debug("Registered item: {}", it.instance.registryName)
		}
	}
}
