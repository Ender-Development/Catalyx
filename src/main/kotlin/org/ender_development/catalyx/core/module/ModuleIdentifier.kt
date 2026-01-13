package org.ender_development.catalyx.core.module

import org.ender_development.catalyx.api.v1.modules.interfaces.IModuleIdentifier

class ModuleIdentifier : IModuleIdentifier {
	override val containerId: String
	override val moduleId: String

	constructor(containerId: String, moduleId: String) {
		this.containerId = containerId
		this.moduleId = moduleId
	}

	/**
	 * Format: "containerId:moduleId"
	 */
	constructor(identifier: String) {
		if(identifier.isBlank())
			error("Identifier is blank")

		val split = identifier.split(':')
		if(split.size != 2)
			error("Identifier does not follow the needed format of 'containerId:moduleId'")

		containerId = split[0]
		moduleId = split[1]
	}

	override fun equals(other: Any?) =
		this === other || (other is ModuleIdentifier && containerId == other.containerId && moduleId == other.moduleId)

	override fun hashCode() =
		toString().hashCode()

	override fun toString() =
		"$containerId:$moduleId"
}
