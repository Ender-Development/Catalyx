package org.ender_development.catalyx.test

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules

@CatalyxModule(moduleID = CatalyxModules.MODULE_TEST, containerID = Reference.MODID, name = "Test Module", description = "A module for testing purposes. Will only work in a development environment.", testModule = true)
class TestModule: BaseCatalyxModule() {
	override val eventBusSubscribers: List<Class<*>>
		get() = listOf(TestEventHandler.javaClass)

	override fun preInit(event: FMLPreInitializationEvent) =
		logger.info("Detected deobfuscated environment, adding some testing features")
}
