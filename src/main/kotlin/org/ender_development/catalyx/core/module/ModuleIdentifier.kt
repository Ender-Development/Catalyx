package org.ender_development.catalyx.core.module

import org.ender_development.catalyx.api.v1.modules.interfaces.IModuleIdentifier

data class ModuleIdentifier(override val containerId: String, override val moduleId: String) : IModuleIdentifier {
	override fun toString() =
		"$containerId:$moduleId"
}
