package org.ender_development.catalyx_.modules

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx_.core.module.ICatalyxModule

/**
 * Abstract base class for all builtin Catalyx modules
 */
internal abstract class CatalyxModuleBase : ICatalyxModule {
	override val logger: Logger = Catalyx.LOGGER
}
