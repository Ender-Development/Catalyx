package org.ender_development.catalyx.test

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.blocks.BaseBlock
import org.ender_development.catalyx.blocks.multiblock.BaseEdge
import org.ender_development.catalyx.blocks.multiblock.BaseMiddleBlock
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.items.BaseItem
import org.ender_development.catalyx.modules.BaseCatalyxModule
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.tiles.BaseMiddleTile
import org.ender_development.catalyx.utils.LoggerUtils

@CatalyxModule(
	moduleID = CatalyxModules.MODULE_TEST,
	containerID = Reference.MODID,
	name = "Test Module",
	description = "A module for testing purposes. Will only work in a development environment.",
	testModule = true
)
internal class TestModule : BaseCatalyxModule() {
	val testItem = BaseItem(Catalyx, "test_item").requires("!${Mods.GROOVYSCRIPT}")
	val testItem2 = BaseItem(Catalyx, "test_item_2").requires(Mods.GROOVYSCRIPT)
	val testBlock = BaseBlock(Catalyx, "test_block")
	val testMulti = BaseEdge(Catalyx, "test_edge")
	val testMultiBlock = BaseMiddleBlock<BaseMiddleTile>(Catalyx, "test_middle", BaseMiddleTile::class.java, 1, testMulti)

	override val logger: Logger = LoggerUtils.new("Development")

	override val eventBusSubscribers: List<Class<*>> = listOf(TestEventHandler::class.java)

	override fun preInit(event: FMLPreInitializationEvent) =
		logger.info("Detected deobfuscated environment, adding some testing features")
}
