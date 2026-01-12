package org.ender_development.catalyx_.modules.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.ender_development.catalyx_.core.Reference
import org.ender_development.catalyx_.modules.integration.IntegrationModule
import org.ender_development.catalyx_.modules.integration.Mods
import org.ender_development.catalyx_.core.module.CatalyxModule
import org.ender_development.catalyx_.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx_.core.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxBuiltinModuleContainer.MODULE_TOP,
	containerId = Reference.MODID,
	modDependencies = [Mods.TOP],
	name = "Catalyx The One Probe Integration Module",
	description = "Adds integration with The One Probe",
	moduleDependencies = ["${Reference.MODID}:${CatalyxBuiltinModuleContainer.MODULE_INTEGRATION}"]
)
internal class ModuleTheOneProbe : IntegrationModule() {
	override val logger = super.logger.subLogger("TheOneProbe")

	override fun init(event: FMLInitializationEvent) {
		logger.info("TheOneProbe found. Enabling integration...")

		TheOneProbe.theOneProbeImp.registerProvider(FluidTileProvider())
	}
}
