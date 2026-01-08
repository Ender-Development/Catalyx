package org.ender_development.catalyx.modules

import org.ender_development.catalyx.Reference

interface IModuleManager {
	fun isModuleEnabled(containerId: String, moduleId: String) =
		isModuleEnabled(ModuleIdentifier(containerId, moduleId))

	fun isModuleEnabled(identifier: ModuleIdentifier): Boolean
	fun registerContainer(container: ICatalyxModuleContainer)

	val activeContainer: ICatalyxModuleContainer?
	val moduleStage: ModuleStage

	fun passedStage(stage: ModuleStage) =
		moduleStage > stage
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun IModuleManager.isModuleEnabled(moduleId: String) =
	isModuleEnabled(ModuleIdentifier(Reference.MODID, moduleId))
