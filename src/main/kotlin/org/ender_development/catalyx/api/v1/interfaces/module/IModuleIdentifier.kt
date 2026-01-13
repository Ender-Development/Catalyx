package org.ender_development.catalyx.api.v1.interfaces.module

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.api.v1.interfaces.cast.IAsResourceLocation

/**
 * An identifier thet identifies a module
 * Any ModuleIdentifier thet implements this, can be casted to a [ResourceLocation]
 * through the [IAsResourceLocation] implementation
 */
interface IModuleIdentifier : IAsResourceLocation {
	val containerId: String
	val moduleId: String
}
