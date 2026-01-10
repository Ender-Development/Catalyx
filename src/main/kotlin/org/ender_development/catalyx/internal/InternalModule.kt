package org.ender_development.catalyx.internal

import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.items.CopyPasteTool
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.ICatalyxModule
import org.ender_development.catalyx.modules.catalyx.CatalyxModules
import org.ender_development.catalyx.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxModules.MODULE_INTERNAL,
	containerId = Reference.MODID,
	name = "Internal",
	description = "An internal module for Catalyx, used for stuff that can can be used in all mods that use Catalyx."
)
class InternalModule() : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Internal")

	val copyPasteTool = CopyPasteTool()
}
