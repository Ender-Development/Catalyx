package org.ender_development.catalyx.modules.integration

import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.core.utils.extensions.subLogger
import org.ender_development.catalyx.modules.CatalyxModuleBase

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_INTEGRATION,
	containerId = Reference.MODID,
	name = "Catalyx Integration Module",
	description = "Adds integration with other mods. Disabling this will disable all integration submodules."
)
internal open class IntegrationModule : CatalyxModuleBase() {
	override val logger = super.logger.subLogger("Integration")
}
