package org.ender_development.catalyx.modules.common

import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.subLogger
import org.ender_development.catalyx.api.v1.modules.annotations.CatalyxModule
import org.ender_development.catalyx.api.v1.modules.interfaces.ICatalyxModule
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.items.CopyPasteTool
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_COMMON,
	containerId = Reference.MODID,
	name = "Common",
	description = "The default module for Catalyx, used for stuff that can can be used in all mods that use Catalyx."
)
class CommonModule() : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Internal")

	val copyPasteTool = CopyPasteTool()
}
