package org.ender_development.catalyx_.modules.internal

import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx_.core.Reference
import org.ender_development.catalyx.items.CopyPasteTool
import org.ender_development.catalyx_.core.module.CatalyxModule
import org.ender_development.catalyx_.core.module.ICatalyxModule
import org.ender_development.catalyx_.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx_.core.utils.extensions.subLogger

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
