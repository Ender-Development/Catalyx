package io.enderdev.catalyx

import io.enderdev.catalyx.client.BlockHighlighter
import io.enderdev.catalyx.integration.top.CatalyxTOPHandler
import io.enderdev.catalyx.network.PacketHandler
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

	@EventHandler
	fun init(e: FMLInitializationEvent) {
		if(Loader.isModLoaded("theoneprobe"))
			CatalyxTOPHandler.init()
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) {
		BlockHighlighter.eventHandler(event)
	}
}
