package org.ender_development.catalyx.modules.test

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.subLogger
import org.ender_development.catalyx.api.v1.modules.annotations.CatalyxModule
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.blocks.BaseTileBlock
import org.ender_development.catalyx.core.blocks.IOTileBlock
import org.ender_development.catalyx.core.blocks.multiblock.CenterBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.CornerBlock
import org.ender_development.catalyx.core.blocks.multiblock.parts.SideBlock
import org.ender_development.catalyx.core.client.AreaHighlighter
import org.ender_development.catalyx.core.tiles.BaseTile
import org.ender_development.catalyx.core.tiles.helper.ICopyPasteExtraDataTile
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.modules.CatalyxModuleBase

@Suppress("unused")
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

	// yes, this needs to be in preInit, otherwise a crash happens because CapabilityEnergy.ENERGY is still null lmao
	override fun preInit(event: FMLPreInitializationEvent) {
		class TestCopyPasteTile() : BaseTile(Catalyx), ICopyPasteExtraDataTile {
			override fun copyData(tag: NBTTagCompound) {
				repeat(10) {
					tag.setInteger("Copy$it", it + Catalyx.RANDOM.nextInt(100, 1000))
				}
			}

			override fun pasteData(tag: NBTTagCompound, player: EntityPlayer) {
				logger.info("Received:")
				logger.info(tag.toString())
			}
		}

		val testCopyPasteBlock = object : BaseTileBlock(Catalyx, "test_copy_paste", TestCopyPasteTile::class.java, -1) {
			override val textureLocation = ResourceLocation(Reference.MODID, "logo")
			override val modelLocation = ResourceLocation("minecraft", "block/cobblestone")

			override fun createTileEntity(world: World, state: IBlockState) =
				TestCopyPasteTile()
		}
	}

	override fun load() =
		logger.info("Detected deobfuscated environment, adding some testing features")

	override val eventBusSubscribers = if(Utils.environment.isClient) listOf(TestEventHandler()) else emptyList()

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
