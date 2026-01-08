package org.ender_development.catalyx.modules

import it.unimi.dsi.fastutil.objects.ObjectSets
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference

internal open class BaseCatalyxModule : ICatalyxModule {
	override val logger: Logger = Catalyx.LOGGER

	override val dependencyUids: Set<ModuleIdentifier> =
		ObjectSets.singleton(ModuleIdentifier(Reference.MODID, CatalyxModules.MODULE_CORE))
}
