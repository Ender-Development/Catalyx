package org.ender_development.catalyx.core

import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ICatalyxModule

@CatalyxModule(moduleID = CatalyxModules.MODULE_CORE, containerID = Reference.MODID, name = "Core", description = "The core module required by all other modules.", coreModule = true)
class CoreModule: ICatalyxModule {
}
