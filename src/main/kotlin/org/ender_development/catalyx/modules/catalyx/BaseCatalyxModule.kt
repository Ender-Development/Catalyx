package org.ender_development.catalyx.modules.catalyx

import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.modules.ICatalyxModule

internal open class BaseCatalyxModule : ICatalyxModule {
	override val logger: Logger = Catalyx.LOGGER
}
