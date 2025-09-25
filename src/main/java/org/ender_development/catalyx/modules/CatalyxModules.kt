package org.ender_development.catalyx.modules

import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.Mods

object CatalyxModules: IModuleContainer {
	override val id = Reference.MODID

	const val MODULE_CORE = "core"
	const val MODULE_TEST = "test"
	const val MODULE_INTEGRATION = "integration"

	// Integration Modules
	const val MODULE_CT = "${MODULE_INTEGRATION}_${Mods.CRAFTTWEAKER}"
	const val MODULE_GRS = "${MODULE_INTEGRATION}_${Mods.GROOVYSCRIPT}"
	const val MODULE_JEI = "${MODULE_INTEGRATION}_${Mods.JEI}"
	const val MODULE_OC = "${MODULE_INTEGRATION}_${Mods.OC}"
	const val MODULE_TOP = "${MODULE_INTEGRATION}_${Mods.TOP}"
}
