package org.ender_development.catalyx.test

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.blocks.multiblock.CenterBlock
import org.ender_development.catalyx.blocks.multiblock.parts.CornerBlock
import org.ender_development.catalyx.blocks.multiblock.parts.SideBlock
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.utils.LoggerUtils
import org.ender_development.catalyx.utils.SideUtils

@CatalyxModule(
	moduleID = CatalyxModules.MODULE_TEST,
	containerID = Reference.MODID,
	name = "Test Module",
	description = "A module for testing purposes. Will only work in a development environment.",
	testModule = true
)
internal class TestModule : BaseCatalyxModule() {
	val testCorner = CornerBlock(Catalyx, "test_corner")
	val testSide = SideBlock(Catalyx, "test_side")
	val testMultiBlock = CenterBlock<DummyClass1>(Catalyx, "test_middle", DummyClass1::class.java, 1, testCorner, testSide)

	override val logger: Logger = LoggerUtils.new("Development")

	override val eventBusSubscribers: List<Class<*>> = if(SideUtils.isClient) listOf(TestEventHandler::class.java) else emptyList()

	override fun preInit(event: FMLPreInitializationEvent) =
		logger.info("Detected deobfuscated environment, adding some testing features")
}
