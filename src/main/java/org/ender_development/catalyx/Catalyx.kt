package org.ender_development.catalyx

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.LogManager
import org.ender_development.catalyx.client.AreaHighlighter
import org.ender_development.catalyx.integration.top.CatalyxTOPHandler
import org.ender_development.catalyx.items.CatalyxModItems
import org.ender_development.catalyx.modules.CatalyxModules
import org.ender_development.catalyx.modules.ModuleContainerRegistryEvent
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.network.PacketHandler
import org.ender_development.catalyx.utils.PersistentData
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
	const val DEPENDENCIES = "required-after:forgelin_continuous@[${Reference.KOTLIN_VERSION},);after:groovyscript@[${Reference.GROOVYSCRIPT_VERSION},);"

	/**
	 * The random number generator used throughout the mod.
	 */
	internal val RANDOM = Random(System.nanoTime())

	/**
	 * The logger for the mod to use. Also, the default logger for modules.
	 * We can't grep the logger from pre-init because some modules may want to use it in construction.
	 */
	internal val LOGGER = LogManager.getLogger(Reference.MOD_NAME)

	internal val ownSettings = CatalyxSettings(Reference.MODID, CreativeTabs.MISC, Catalyx, true, { }, { CatalyxModItems.items.add(it) })

	internal lateinit var moduleManager: ModuleManager

	@EventHandler
	fun construction(e: FMLConstructionEvent) {
		PersistentData.init()
		moduleManager = ModuleManager.instance
		moduleManager.registerContainer(CatalyxModules)
		MinecraftForge.EVENT_BUS.post(ModuleContainerRegistryEvent())
		moduleManager.setup(e.asmHarvestedData)
		moduleManager.construction(e)
	}

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		moduleManager.preInit(e)
		PacketHandler.init()
	}

	@EventHandler
	fun init(e: FMLInitializationEvent) {
		moduleManager.init(e)
		if(Loader.isModLoaded("theoneprobe"))
			CatalyxTOPHandler.init()
	}

	@EventHandler
	fun postInit(e: FMLPostInitializationEvent) =
		moduleManager.postInit(e)

	@EventHandler
	fun loadComplete(e: FMLLoadCompleteEvent) =
		moduleManager.loadComplete(e)

	@EventHandler
	fun serverAboutToStart(e: FMLServerAboutToStartEvent) =
		moduleManager.serverAboutToStart(e)

	@EventHandler
	fun serverStarting(e: FMLServerStartingEvent) =
		moduleManager.serverStarting(e)

	@EventHandler
	fun serverStarted(e: FMLServerStartedEvent) =
		moduleManager.serverStarted(e)

	@EventHandler
	fun serverStopping(e: FMLServerStoppingEvent) =
		moduleManager.serverStopping(e)

	@EventHandler
	fun serverStopped(e: FMLServerStoppedEvent) =
		moduleManager.serverStopped(e)

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
