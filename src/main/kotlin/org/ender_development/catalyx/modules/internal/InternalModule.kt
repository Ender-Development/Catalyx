package org.ender_development.catalyx.modules.internal

import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.items.CopyPasteTool
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.module.ICatalyxModule
import org.ender_development.catalyx.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx.core.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxBuiltinModuleContainer.MODULE_INTERNAL,
	containerId = Reference.MODID,
	name = "Internal",
	description = "An internal module for Catalyx, used for stuff that can can be used in all mods that use Catalyx."
)
class InternalModule() : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Internal")

	val copyPasteTool = CopyPasteTool()
}
