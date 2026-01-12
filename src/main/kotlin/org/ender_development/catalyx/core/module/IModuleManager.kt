package org.ender_development.catalyx.core.module

interface IModuleManager {
	fun isModuleEnabled(containerId: String, moduleId: String) =
		isModuleEnabled(ModuleIdentifier(containerId, moduleId))

	fun isModuleEnabled(identifier: ModuleIdentifier): Boolean
	fun isModuleEnabled(module: ICatalyxModule): Boolean

	fun registerContainer(container: Any)

	val activeContainer: Any?
}
