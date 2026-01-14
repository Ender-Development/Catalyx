package org.ender_development.catalyx.api.v1.modules.interfaces

import net.minecraft.util.ResourceLocation

/**
 * An identifier thet identifies a module
 */
interface IModuleIdentifier {
	val containerId: String
	val moduleId: String

	/**
	 * Convert this to a [ResourceLocation], for whatever reason.
	 */
	fun toResourceLocation() =
		ResourceLocation(containerId, moduleId)
}

/*
// TODO: Discuss that the flowing cloud be a batter solution, that cold replace the ModuleIdentifier BS:
typealias ModuleIdentifier = ResourceLocation
inline val ModuleIdentifier.containerId: String?
	get() = this.namespace

inline val ModuleIdentifier.moduleId: String?
	get() = this.path
*/
