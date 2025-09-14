package org.ender_development.catalyx

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.client.AreaHighlighter
import org.ender_development.catalyx.integration.top.CatalyxTOPHandler
import org.ender_development.catalyx.network.PacketHandler
import org.ender_development.catalyx.test.TestEventHandler
import kotlin.random.Random

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

	/**
	 * The random number generator used throughout the mod.
	 */
	val RANDOM = Random(System.currentTimeMillis())

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

		// similar approach to FML's CoreModManager#L208
		try {
			ClassLoader.getSystemClassLoader().loadClass("net.minecraft.world.World")
			logger.info("Catalyx: Detected dev environment, adding some testing features")
			MinecraftForge.EVENT_BUS.register(TestEventHandler.instance)
		} catch(_: Exception) {}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) {
		AreaHighlighter.eventHandlers.forEach { it(event) }
	}
}
