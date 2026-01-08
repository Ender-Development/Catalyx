package org.ender_development.catalyx.modules

import net.minecraft.util.ResourceLocation

class ModuleIdentifier(
	@Suppress("unused")
	val containerId: String,

	@Suppress("unused")
	val moduleId: String
) : ResourceLocation(containerId, moduleId)
