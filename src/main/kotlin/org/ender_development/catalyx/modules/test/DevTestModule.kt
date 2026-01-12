package org.ender_development.catalyx.modules.test

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.core.Catalyx
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.blocks.IOTileBlock
import org.ender_development.catalyx.core.blocks.multiblock.CenterBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.CornerBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.SideBlock
import org.ender_development.catalyx.core.client.AreaHighlighter
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.utils.SideUtils
import org.ender_development.catalyx.core.utils.extensions.subLogger
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.modules.CatalyxModuleBase

@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_TEST,
	containerId = Reference.MODID,
	name = "Test Module",
	description = "A module for development/testing purposes. Will only work in a development (deobfuscated) environment.",
	moduleDependencies = ["${Reference.MODID}:${CatalyxInternalModuleContainer.MODULE_CORE}"],
	testModule = true
)
internal class DevTestModule : CatalyxModuleBase() {
	override val logger = super.logger.subLogger("Development")

	val testCorner = CornerBlock(Catalyx, "test_corner")
	val testSide = SideBlock(Catalyx, "test_side")
	val testMultiBlock = CenterBlock(Catalyx, "test_middle", DummyClass1::class.java, 1, testCorner, testSide)
	val testTesrBlock = IOTileBlock(Catalyx, "test_tesr", DummyClass2::class.java, 0)

	override fun load() =
		logger.info("Detected deobfuscated environment, adding some testing features")

	override val eventBusSubscribers = if(SideUtils.isClient) listOf(TestEventHandler()) else emptyList()

	class TestEventHandler {
		val areaHighlighter = AreaHighlighter()

		@SubscribeEvent
		fun onChat(ev: ClientChatEvent) {
			if(!ev.message.startsWith($$"$c.h "))
				return

			val split = ev.message.removePrefix($$"$c.h ").split(" ")
			try {
				if(split[0][0] == 'T') {
					areaHighlighter.thickness = split[0].substring(1).toFloat()
					return
				}
				val x1 = split[0].toDouble()
				val y1 = split[1].toDouble()
				val z1 = split[2].toDouble()
				val x2 = split[3].toDouble()
				val y2 = split[4].toDouble()
				val z2 = split[5].toDouble()
				val r = split[6].toFloat()
				val g = split[7].toFloat()
				val b = split[8].toFloat()
				val time = split[9].toInt()
				areaHighlighter.highlightArea(x1, y1, z1, x2, y2, z2, r, g, b, time)
			} catch(e: Exception) {
				Minecraft.getMinecraft().player.sendMessage(TextComponentString($$"usage: $c.h x1 y1 z1 z2 y2 z2 r g b time\nor: usage: $c.h T<thickness>\ngot: $$e"))
				e.printStackTrace()
			}
		}
	}
}
