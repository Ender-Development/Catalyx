package org.ender_development.catalyx.core.module

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.interfaces.module.IModuleIdentifier

data class ModuleIdentifier(
	override val containerId: String,
	override val moduleId: String): IModuleIdentifier {

	override fun asResourceLocation(): ResourceLocation
		= ResourceLocation(containerId, moduleId)
}

