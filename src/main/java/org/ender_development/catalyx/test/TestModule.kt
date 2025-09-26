package org.ender_development.catalyx.test

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(
	moduleID = CatalyxModules.MODULE_TEST,
	containerID = Reference.MODID,
	name = "Test Module",
	description = "A module for testing purposes. Will only work in a development environment.",
	testModule = true
)
class TestModule : BaseCatalyxModule() {
	override val logger: Logger = LoggerUtils.new("Development")

	override val eventBusSubscribers: List<Class<*>> = listOf(TestEventHandler::class.java)

	override fun preInit(event: FMLPreInitializationEvent) =
		logger.info("Detected deobfuscated environment, adding some testing features")
}
