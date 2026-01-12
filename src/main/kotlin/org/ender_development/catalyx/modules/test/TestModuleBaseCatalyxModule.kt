package org.ender_development.catalyx.modules.test

import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.blocks.IOTileBlock
import org.ender_development.catalyx.core.blocks.multiblock.CenterBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.CornerBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.SideBlock
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModuleBase
import org.ender_development.catalyx.modules.CatalyxBuiltinModuleContainer
import org.ender_development.catalyx.core.utils.SideUtils
import org.ender_development.catalyx.core.utils.extensions.subLogger

@CatalyxModule(
	moduleId = CatalyxBuiltinModuleContainer.MODULE_TEST,
	containerId = Reference.MODID,
	name = "Test Module",
	description = "A module for testing purposes. Will only work in a development environment.",
	moduleDependencies = ["${Reference.MODID}:${CatalyxBuiltinModuleContainer.MODULE_CORE}"],
	testModule = true
)
internal class TestModuleBaseCatalyxModule : CatalyxModuleBase() {
	override val logger = super.logger.subLogger("Development")

	val testCorner = CornerBlock(Catalyx, "test_corner")
	val testSide = SideBlock(Catalyx, "test_side")
	val testMultiBlock = CenterBlock(Catalyx, "test_middle", DummyClass1::class.java, 1, testCorner, testSide)
	val testTesrBlock = IOTileBlock(Catalyx, "test_tesr", DummyClass2::class.java, 0)

	override fun load() =
		logger.info("Detected deobfuscated environment, adding some testing features")

	override val eventBusSubscribers: List<Class<*>> = if(SideUtils.isClient) listOf(TestEventHandler::class.java) else emptyList()
}
