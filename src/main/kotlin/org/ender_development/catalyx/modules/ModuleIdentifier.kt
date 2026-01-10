package org.ender_development.catalyx.modules

import net.minecraft.util.ResourceLocation

class ModuleIdentifier : ResourceLocation {
	val containerId: String = namespace
	val moduleId: String = path

	constructor(identifier: String) : super(identifier)
	constructor(containerId: String, moduleId: String) : super(containerId, moduleId)
}
