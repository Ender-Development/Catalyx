package io.enderdev.catalyx

import io.enderdev.catalyx.client.BlockHighlighter
import io.enderdev.catalyx.network.PacketHandler
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.Logger

@Mod(
	modid = Reference.MODID,
	name = Reference.MOD_NAME,
	version = Reference.VERSION,
	dependencies = Catalyx.DEPENDENCIES,
	modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
	acceptableRemoteVersions = "*"
)
@Mod.EventBusSubscriber(modid = Reference.MODID)
object Catalyx {
	const val DEPENDENCIES = "required-after:forgelin_continuous@[${Reference.KOTLIN_VERSION},);"

	internal lateinit var logger: Logger

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		logger = e.modLog
		PacketHandler.init()
	}

	@SubscribeEvent
	fun renderWorldLast(event: RenderWorldLastEvent) {
		BlockHighlighter.eventHandler(event)
	}
}
