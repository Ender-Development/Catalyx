package org.ender_development.catalyx.integration

import it.unimi.dsi.fastutil.objects.ObjectSets
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules

/**
 * Abstract class meant to be used by mod-specific compatibility modules.
 * Implements some shared skeleton code that should be shared by other modules.
 */
internal abstract class IntegrationSubmodule : BaseCatalyxModule() {
	override val dependencyUids: Set<ResourceLocation>
		get() = ObjectSets.singleton(ResourceLocation(Reference.MODID, CatalyxModules.MODULE_INTEGRATION))
}
