package org.ender_development.catalyx.integration

import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules

@CatalyxModule(moduleID = CatalyxModules.MODULE_INTEGRATION, containerID = Reference.MODID, name = "Catalyx Integration Module", description = "Adds integration with other mods. Disabling this will disable all integration submodules.")
class IntegrationModule: BaseCatalyxModule() {
}
