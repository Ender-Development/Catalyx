package org.ender_development.catalyx.integration

import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.catalyx.BaseCatalyxModule
import org.ender_development.catalyx.modules.catalyx.CatalyxModules
import org.ender_development.catalyx.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxModules.MODULE_INTEGRATION,
	containerId = Reference.MODID,
	name = "Catalyx Integration Module",
	description = "Adds integration with other mods. Disabling this will disable all integration submodules."
)
internal class IntegrationModule : BaseCatalyxModule() {
	override val logger = super.logger.subLogger("Integration")
}
