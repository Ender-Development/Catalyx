package org.ender_development.catalyx.api.v1.modules

/**
 * API-Status: NOT-FROZEN
 * Be aware
 */

import org.ender_development.catalyx.api.v1.modules.interfaces.IModuleIdentifier
import org.ender_development.catalyx.api.v1.modules.interfaces.IModuleManager
import org.ender_development.catalyx.core.module.ModuleIdentifier
import org.ender_development.catalyx.core.module.ModuleManager

object Modules {
	/**
	 * Contains the current [ModuleManager] implementation
	 */
	val moduleManager: IModuleManager = ModuleManager

	/**
	 * Factory for [IModuleManager], currently implemented by [ModuleIdentifier]
	 */
	fun newModuleIdentifier(containerId: String, moduleId: String): IModuleIdentifier =
		ModuleIdentifier(containerId, moduleId)

	/**
	 * Factory for [IModuleManager], currently implemented by [ModuleIdentifier]
	 */
	fun newModuleIdentifier(identifier: String): IModuleIdentifier =
		ModuleIdentifier(identifier)
}
