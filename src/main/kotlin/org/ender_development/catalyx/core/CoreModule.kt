package org.ender_development.catalyx.core

import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ICatalyxModule
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(
	moduleId = CatalyxModules.MODULE_CORE,
	containerId = Reference.MODID,
	name = "Core",
	description = "The core module required by all other modules from Catalyx.",
	coreModule = true
)
internal class CoreModule(override val logger: Logger = LoggerUtils.new("Core")) : ICatalyxModule {
	override val eventBusSubscribers: List<Class<*>> = listOf(CoreEventHandler::class.java)

	override fun serverAboutToStart(event: FMLServerAboutToStartEvent) =
		CoreEventHandler.serverAboutToStart(event)

	override fun serverStopped(event: FMLServerStoppedEvent) =
		CoreEventHandler.serverStopped(event)
}
