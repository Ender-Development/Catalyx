package org.ender_development.catalyx.modules

import net.minecraft.util.ResourceLocation

private typealias ContainerId = String
private typealias ModuleId = String

class ModuleIdentifier(
	@Suppress("unused")
	val containerId: ContainerId,

	@Suppress("unused")
	val moduleId: ModuleId
) : ResourceLocation(containerId, moduleId)
