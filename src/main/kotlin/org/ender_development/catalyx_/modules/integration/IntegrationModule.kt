package org.ender_development.catalyx_.modules.integration

import org.ender_development.catalyx_.core.Reference
import org.ender_development.catalyx_.core.module.CatalyxModule
import org.ender_development.catalyx_.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx_.core.utils.extensions.subLogger
import org.ender_development.catalyx_.modules.CatalyxModuleBase

@CatalyxModule(
	moduleId = CatalyxBuiltinModuleContainer.MODULE_INTEGRATION,
	containerId = Reference.MODID,
	name = "Catalyx Integration Module",
	description = "Adds integration with other mods. Disabling this will disable all integration submodules."
)
internal open class IntegrationModule : CatalyxModuleBase() {
	override val logger = super.logger.subLogger("Integration")
}
