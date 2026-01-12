package org.ender_development.catalyx.modules

import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.utils.Mods
import org.ender_development.catalyx.core.module.CatalyxModuleContainer

/**
 * Module Container for all internal Catalyx modules
 */
@CatalyxModuleContainer(Reference.MODID, Reference.MODID)
object CatalyxInternalModuleContainer {
	const val MODULE_CORE = "core"
	const val MODULE_INTERNAL = "internal"
	const val MODULE_TEST = "test"
	const val MODULE_INTEGRATION = "integration"

	// Integration Modules
	const val MODULE_GRS = "${MODULE_INTEGRATION}_${Mods.GROOVYSCRIPT}"
	const val MODULE_CT = "${MODULE_INTEGRATION}_${Mods.CRAFTTWEAKER}"
	const val MODULE_JEI = "${MODULE_INTEGRATION}_${Mods.JEI}"
	const val MODULE_OC = "${MODULE_INTEGRATION}_${Mods.OC}"
	const val MODULE_TOP = "${MODULE_INTEGRATION}_${Mods.TOP}"
}
