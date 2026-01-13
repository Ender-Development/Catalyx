package org.ender_development.catalyx.core.module

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.interfaces.module.IModuleIdentifier

class ModuleIdentifier : ResourceLocation, IModuleIdentifier {
	override val containerId: String = namespace
	override val moduleId: String = path

	constructor(identifier: String) : super(identifier)
	constructor(containerId: String, moduleId: String) : super(containerId, moduleId)

	// needed to select an implementation
	override fun compareTo(other: ResourceLocation): Int = super<ResourceLocation>.compareTo(other)

	override fun asResourceLocation() = this
}
