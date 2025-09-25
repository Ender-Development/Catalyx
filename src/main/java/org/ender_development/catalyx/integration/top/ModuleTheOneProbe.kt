package org.ender_development.catalyx.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.IntegrationSubmodule
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules

@CatalyxModule(moduleID = CatalyxModules.MODULE_TOP, containerID = Reference.MODID, modDependencies = [Mods.TOP], name = "Catalyx The One Probe Integration Module", description = "Adds integration with The One Probe")
class ModuleTheOneProbe: IntegrationSubmodule() {
	override fun init(event: FMLInitializationEvent) {
		logger.info("TheOneProbe found. Enabling integration...");
		val top = TheOneProbe.theOneProbeImp
		top.registerProvider(FluidTileProvider())
	}
}
