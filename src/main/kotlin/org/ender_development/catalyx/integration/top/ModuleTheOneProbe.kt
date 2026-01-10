package org.ender_development.catalyx.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.IntegrationModule
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.catalyx.CatalyxModules
import org.ender_development.catalyx.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxModules.MODULE_TOP,
	containerId = Reference.MODID,
	modDependencies = [Mods.TOP],
	name = "Catalyx The One Probe Integration Module",
	description = "Adds integration with The One Probe",
	moduleDependencies = ["${Reference.MODID}:${CatalyxModules.MODULE_INTEGRATION}"]
)
internal class ModuleTheOneProbe : IntegrationModule() {
	override val logger = super.logger.subLogger("TheOneProbe")

	override fun init(event: FMLInitializationEvent) {
		logger.info("TheOneProbe found. Enabling integration...")

		TheOneProbe.theOneProbeImp.registerProvider(FluidTileProvider())
	}
}
