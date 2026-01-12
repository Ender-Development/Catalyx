package org.ender_development.catalyx_.modules.coremodule

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx_.core.Reference
import org.ender_development.catalyx_.core.client.AreaHighlighter
import org.ender_development.catalyx_.core.module.CatalyxModule
import org.ender_development.catalyx_.core.module.ICatalyxModule
import org.ender_development.catalyx_.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx_.core.utils.extensions.subLogger
import org.ender_development.catalyx_.core.utils.persistence.WorldPersistentData

@CatalyxModule(
	moduleId = CatalyxBuiltinModuleContainer.MODULE_CORE,
	containerId = Reference.MODID,
	name = "Core",
	description = "The core module required by all other modules from Catalyx.",
	coreModule = true
)
internal class CoreModule : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Core")

	override val eventBusSubscribers = listOf(this)

	override fun serverAboutToStart(event: FMLServerAboutToStartEvent) =
		WorldPersistentData.instances.forEach(WorldPersistentData::worldJoined)

	override fun serverStopped(event: FMLServerStoppedEvent) =
		WorldPersistentData.instances.forEach(WorldPersistentData::worldLeft)

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) =
		AreaHighlighter.eventHandlers.forEach { it(event) }
}
