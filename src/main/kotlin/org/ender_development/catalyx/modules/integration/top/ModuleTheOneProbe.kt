package org.ender_development.catalyx.modules.integration.top

import mcjty.theoneprobe.TheOneProbe
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.utils.Mods
import org.ender_development.catalyx.core.utils.extensions.subLogger
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.modules.integration.IntegrationModule

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_TOP,
	containerId = Reference.MODID,
	modDependencies = [Mods.TOP],
	name = "Catalyx The One Probe Integration Module",
	description = "Adds integration with The One Probe",
	moduleDependencies = ["${Reference.MODID}:${CatalyxInternalModuleContainer.MODULE_INTEGRATION}"]
)
internal class ModuleTheOneProbe : IntegrationModule() {
	override val logger = super.logger.subLogger("TheOneProbe")

	override fun init(event: FMLInitializationEvent) {
		logger.info("TheOneProbe found. Enabling integration...")

		TheOneProbe.theOneProbeImp.registerProvider(FluidTileProvider())
	}
}
