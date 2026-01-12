package org.ender_development.catalyx.modules.internal

import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.items.CopyPasteTool
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.module.ICatalyxModule
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.core.utils.extensions.subLogger

// TODO rename, this name is silly, but couldn't come up with a better one right meow
@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_INTERNAL,
	containerId = Reference.MODID,
	name = "Internal",
	description = "An internal module for Catalyx, used for stuff that can can be used in all mods that use Catalyx."
)
class InternalModule() : ICatalyxModule {
	override val logger = Catalyx.LOGGER.subLogger("Internal")

	val copyPasteTool = CopyPasteTool()
}
