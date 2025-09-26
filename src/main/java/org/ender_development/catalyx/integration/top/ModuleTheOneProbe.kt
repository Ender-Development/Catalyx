package org.ender_development.catalyx.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.IntegrationSubmodule
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(moduleID = CatalyxModules.MODULE_TOP, containerID = Reference.MODID, modDependencies = [Mods.TOP], name = "Catalyx The One Probe Integration Module", description = "Adds integration with The One Probe")
class ModuleTheOneProbe : IntegrationSubmodule() {
	override val logger: Logger = LoggerUtils.new("TheOneProbe")

	override fun init(event: FMLInitializationEvent) {
		logger.info("TheOneProbe found. Enabling integration...")
		val top = TheOneProbe.theOneProbeImp
		top.registerProvider(FluidTileProvider())
	}
}
