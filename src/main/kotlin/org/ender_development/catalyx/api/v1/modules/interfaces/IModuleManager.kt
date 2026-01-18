package org.ender_development.catalyx.api.v1.modules.interfaces

import org.ender_development.catalyx.api.v1.modules.Modules

/**
 * Interface for the actual [ModuleManager][org.ender_development.catalyx.core.module.ModuleManager]
 *
 * @see [org.ender_development.catalyx.api.v1.modules.Modules.moduleManager]
 */
interface IModuleManager {
	/**
	 * Check if a module with the given [containerId] and [moduleId] is enabled
	 */
	fun isModuleEnabled(containerId: String, moduleId: String) =
		isModuleEnabled(Modules.newModuleIdentifier(containerId, moduleId))

	/**
	 * Check if a module with the given [identifier] is enabled
	 */
	fun isModuleEnabled(identifier: IModuleIdentifier): Boolean

	/**
	 * Checkup if a given module is enabled
	 */
	fun isModuleEnabled(module: ICatalyxModule): Boolean

	/**
	 * Registers a Module Container
	 */
	fun registerContainer(container: Any)

	/**
	 * The active Module Container instance, if any.
	 *
	 * Set during initialisation and lifecycle calls.
	 */
	val activeContainer: Any?
}
