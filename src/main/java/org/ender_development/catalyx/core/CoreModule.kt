package org.ender_development.catalyx.core

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ICatalyxModule
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(moduleID = CatalyxModules.MODULE_CORE, containerID = Reference.MODID, name = "Core", description = "The core module required by all other modules from Catalyx.", coreModule = true)
class CoreModule(override val logger: Logger = LoggerUtils.new("Core")) : ICatalyxModule
