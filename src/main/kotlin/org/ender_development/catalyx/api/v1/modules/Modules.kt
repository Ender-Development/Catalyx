package org.ender_development.catalyx.api.v1.modules

/**
 * API-Status: NOT-FROZEN
 * Beware
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
	fun newModuleIdentifier(identifier: String): IModuleIdentifier {
		if(identifier.isBlank())
			error("Identifier is blank")

		val split = identifier.split(':')
		if(split.size != 2)
			error("Identifier does not follow the required format of 'containerId:moduleId'")

		return ModuleIdentifier(split[0], split[1])
	}
}
