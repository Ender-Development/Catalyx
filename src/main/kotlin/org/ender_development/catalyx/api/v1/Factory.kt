package org.ender_development.catalyx.api.v1

/**
 * API-Status: NOT-FROZEN
 * Be aware
 */

import org.ender_development.catalyx.api.v1.interfaces.module.IModuleIdentifier
import org.ender_development.catalyx.api.v1.interfaces.module.IModuleManager
import org.ender_development.catalyx.core.module.ModuleIdentifier
import org.ender_development.catalyx.core.module.ModuleManager

/**
 * Contains the current [ModuleManager] implementation
 */
val moduleManager: IModuleManager = ModuleManager

/**
 * Factories for [IModuleIdentifier]
 * currently implemented by [ModuleIdentifier]
 */
fun newModuleIdentifier(identifier: String): IModuleIdentifier = ModuleIdentifier(identifier)
fun newModuleIdentifier(containerId: String, moduleId: String): IModuleIdentifier = ModuleIdentifier(containerId, moduleId)
