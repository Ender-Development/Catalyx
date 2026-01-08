package org.ender_development.catalyx.integration

import it.unimi.dsi.fastutil.objects.ObjectSets
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ModuleIdentifier

/**
 * Abstract class meant to be used by mod-specific compatibility modules.
 * Implements some shared skeleton code that should be shared by other modules.
 */
internal abstract class IntegrationSubmodule : BaseCatalyxModule() {
	override val dependencyUids: Set<ModuleIdentifier>
		get() = ObjectSets.singleton(ModuleIdentifier(Reference.MODID, CatalyxModules.MODULE_INTEGRATION))
}
