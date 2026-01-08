package org.ender_development.catalyx.integration

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.catalyx.CatalyxModules
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(
	moduleId = CatalyxModules.MODULE_INTEGRATION,
	containerId = Reference.MODID,
	name = "Catalyx Integration Module",
	description = "Adds integration with other mods. Disabling this will disable all integration submodules."
)
internal class IntegrationModule(override val logger: Logger = LoggerUtils.new("Integration")) : BaseCatalyxModule()
