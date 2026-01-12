package org.ender_development.catalyx.modules

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.client.AreaHighlighter
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.module.ICatalyxModule
import org.ender_development.catalyx.core.utils.extensions.subLogger
import org.ender_development.catalyx.core.utils.persistence.WorldPersistentData

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_CORE,
	containerId = Reference.MODID,
	name = "Core",
	description = "The core module required by all other modules from Catalyx.",
	coreModule = true
)
internal class CatalyxCoreModule : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Core")

	override val eventBusSubscribers = listOf(this)

	override fun serverAboutToStart(event: FMLServerAboutToStartEvent) =
		WorldPersistentData.Companion.instances.forEach(WorldPersistentData::worldJoined)

	override fun serverStopped(event: FMLServerStoppedEvent) =
		WorldPersistentData.Companion.instances.forEach(WorldPersistentData::worldLeft)

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) =
		AreaHighlighter.Companion.eventHandlers.forEach { it(event) }
}
