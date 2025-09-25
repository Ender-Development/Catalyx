package org.ender_development.catalyx.modules

import it.unimi.dsi.fastutil.objects.ObjectSets
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.Reference

open class BaseCatalyxModule: ICatalyxModule {
	override val dependencyUids: Set<ResourceLocation> =
		ObjectSets.singleton(ResourceLocation(Reference.MODID, CatalyxModules.MODULE_CORE))
}
