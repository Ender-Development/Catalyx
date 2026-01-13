package org.ender_development.catalyx.api.v1.interfaces.module

import org.ender_development.catalyx.api.v1.newModuleIdentifier

/**
 * Interface for the actual ModulaManager
 *
 * @see [org.ender_development.catalyx.api.v1.moduleManager]
 */
interface IModuleManager {
	/**
	 * Checkup if a given Module is enabled
	 *
	 * @param containerId The ID of the module container witch the module contains
	 * @param moduleId The id of the module to check for
	 * @return true if enabled else false
	 */
	fun isModuleEnabled(containerId: String, moduleId: String) =
		isModuleEnabled(newModuleIdentifier(containerId, moduleId))

	/**
	 * Checkup if a given Module is enabled
	 *
	 * @param identifier the module identifier to check for
	 * @return true if enabled else false
	 */
	fun isModuleEnabled(identifier: IModuleIdentifier): Boolean

	/**
	 * Checkup if module is enabled
	 *
	 * @param module the module object
	 * @return true if enabled else false
	 */
	fun isModuleEnabled(module: ICatalyxModule): Boolean

	/**
	 * Registers a Module Container
	 * TODO: Provide better documentation
	 */
	fun registerContainer(container: Any)

	/**
	 * TODO: Provide any documentation
	 */
	val activeContainer: Any?
}
