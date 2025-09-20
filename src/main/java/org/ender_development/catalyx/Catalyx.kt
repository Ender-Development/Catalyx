package org.ender_development.catalyx

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
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
import org.ender_development.catalyx.items.CatalyxModItems
import org.ender_development.catalyx.network.PacketHandler
import org.ender_development.catalyx.test.TestEventHandler
import org.ender_development.catalyx.utils.DevUtils
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
	internal val RANDOM = Random(System.nanoTime())

	internal lateinit var logger: Logger
	internal val ownSettings = CatalyxSettings(Reference.MODID, CreativeTabs.MISC, Catalyx, true, {  }, { CatalyxModItems.items.add(it) } )

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		logger = e.modLog
		PacketHandler.init()
	}

	@EventHandler
	fun init(e: FMLInitializationEvent) {
		if(Loader.isModLoaded("theoneprobe"))
			CatalyxTOPHandler.init()

		if(DevUtils.isDeobfuscated) {
			logger.info("Catalyx: Detected deobfuscated environment, adding some testing features")
			MinecraftForge.EVENT_BUS.register(TestEventHandler)
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) {
		AreaHighlighter.eventHandlers.forEach { it(event) }
	}

	@SubscribeEvent
	fun registerItems(event: RegistryEvent.Register<Item>) {
		CatalyxModItems.registerItems(event)
	}
}
