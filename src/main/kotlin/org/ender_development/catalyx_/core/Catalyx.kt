package org.ender_development.catalyx_.core

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.ender_development.catalyx_.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx_.core.module.ModuleManager
import org.ender_development.catalyx_.core.network.PacketHandler
import org.ender_development.catalyx_.core.utils.persistence.ConfigPersistentData
import kotlin.random.Random

@Mod(
	modid = Reference.MODID,
	name = Reference.MOD_NAME,
	version = Reference.VERSION,
	dependencies = ICatalyxMod.Companion.DEPENDENCIES,
	modLanguageAdapter = ICatalyxMod.Companion.MOD_LANGUAGE_ADAPTER,
	acceptableRemoteVersions = "*"
)
@Mod.EventBusSubscriber(modid = Reference.MODID)
object Catalyx : ICatalyxMod {
	/**
	 * The random number generator used throughout the mod.
	 */
	internal val RANDOM = Random(System.nanoTime())

	/**
	 * The logger for the mod to use. We can't grep the logger from pre-init,
	 * because some modules may want to use it in construction.
	 */
	internal val LOGGER = LogManager.getLogger(Reference.MOD_NAME)

	override val creativeTab: CreativeTabs = CreativeTabs.MISC
	internal val configPersistentData = ConfigPersistentData(ResourceLocation(Reference.MODID, "recipes"))

	@Mod.EventHandler
	fun construction(e: FMLConstructionEvent) {
		ModuleManager.setup(e.asmHarvestedData)
	}

	@Mod.EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		PacketHandler.init()
	}
}
