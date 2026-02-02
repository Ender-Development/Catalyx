package org.ender_development.catalyx.modules.integration.crafttweaker

import crafttweaker.CraftTweakerAPI
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.ender_development.catalyx.api.v1.common.Mods
import org.ender_development.catalyx.api.v1.common.extensions.subLogger
import org.ender_development.catalyx.api.v1.modules.annotations.CatalyxModule
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.modules.integration.IntegrationModule
import kotlin.properties.Delegates

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_CT,
	containerId = Reference.MODID,
	modDependencies = [Mods.CRAFTTWEAKER],
	name = "Catalyx CraftTweaker Integration Module",
	description = "Adds CT bindings to content creation functions",
	moduleDependencies = ["${Reference.MODID}:${CatalyxInternalModuleContainer.MODULE_INTEGRATION}"]
)
internal class ModuleCraftTweaker : IntegrationModule() {
	override val logger = super.logger.subLogger("CraftTweaker")

	var scriptsSuccessful by Delegates.notNull<Boolean>()

	override fun preInit(event: FMLPreInitializationEvent) {
		logger.info("CraftTweaker found. Loading scripts...")
		CraftTweakerAPI.logInfo("${Reference.MOD_NAME} says meow :3")
		scriptsSuccessful = CraftTweakerAPI.tweaker.loadScript(false, Reference.MODID);
	}
}
