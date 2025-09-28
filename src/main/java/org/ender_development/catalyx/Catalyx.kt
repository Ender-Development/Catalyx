package org.ender_development.catalyx

import net.minecraft.creativetab.CreativeTabs
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.*
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.network.PacketHandler
import org.ender_development.catalyx.utils.LoggerUtils
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
	 * The logger for the mod to use. We can't grep the logger from pre-init,
	 * because some modules may want to use it in construction.
	 */
	internal val LOGGER = LoggerUtils.logger

	internal val ownSettings = CatalyxSettings(Reference.MODID, CreativeTabs.MISC, Catalyx, true)

	@EventHandler
	fun construction(e: FMLConstructionEvent) {
		ModuleManager.setup(e.asmHarvestedData)
		ModuleManager.construction(e)
	}

	@EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		ModuleManager.preInit(e)
		PacketHandler.init()
	}

	@EventHandler
	fun init(e: FMLInitializationEvent) =
		ModuleManager.init(e)

	@EventHandler
	fun postInit(e: FMLPostInitializationEvent) =
		ModuleManager.postInit(e)

	@EventHandler
	fun loadComplete(e: FMLLoadCompleteEvent) =
		ModuleManager.loadComplete(e)

	@EventHandler
	fun serverAboutToStart(e: FMLServerAboutToStartEvent) =
		ModuleManager.serverAboutToStart(e)

	@EventHandler
	fun serverStarting(e: FMLServerStartingEvent) =
		ModuleManager.serverStarting(e)

	@EventHandler
	fun serverStarted(e: FMLServerStartedEvent) =
		ModuleManager.serverStarted(e)

	@EventHandler
	fun serverStopping(e: FMLServerStoppingEvent) =
		ModuleManager.serverStopping(e)

	@EventHandler
	fun serverStopped(e: FMLServerStoppedEvent) =
		ModuleManager.serverStopped(e)
}
