package org.ender_development.catalyx.modules

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.modules.interfaces.ICatalyxModule

/**
 * Abstract base class for all builtin Catalyx modules
 */
internal abstract class CatalyxModuleBase : ICatalyxModule {
	override val logger: Logger = Catalyx.LOGGER
}
