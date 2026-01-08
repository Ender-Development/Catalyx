package org.ender_development.catalyx.modules.catalyx

import it.unimi.dsi.fastutil.objects.ObjectSets
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference

internal open class BaseCatalyxModule : org.ender_development.catalyx.modules.ICatalyxModule {
	override val logger: Logger = Catalyx.LOGGER

	override val dependencyUids: Set<org.ender_development.catalyx.modules.ModuleIdentifier> =
		ObjectSets.singleton(_root_ide_package_.org.ender_development.catalyx.modules.ModuleIdentifier(Reference.MODID, CatalyxModules.MODULE_CORE))
}
