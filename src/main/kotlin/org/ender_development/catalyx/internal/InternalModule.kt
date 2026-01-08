package org.ender_development.catalyx.internal

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.items.CopyPasteTool
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ICatalyxModule
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(moduleId = CatalyxModules.MODULE_INTERNAL, containerId = Reference.MODID, name = "Internal Module", description = "An internal module for Catalyx, used for stuff that can can be used in all mods that use Catalyx.")
class InternalModule(override val logger: Logger = LoggerUtils.new("Internal")) : ICatalyxModule {
	val copyPasteTool = CopyPasteTool()
}
