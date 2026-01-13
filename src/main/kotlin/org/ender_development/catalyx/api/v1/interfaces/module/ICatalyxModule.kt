package org.ender_development.catalyx.api.v1.interfaces.module

import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.api.v1.moduleManager

/**
 * All modules must implement this interface.
 *
 * Provides methods for responding to FML lifecycle events and adding event bus subscribers.
 *
 * Note: If your Module is a kotlin `object`, and other parts of your code access it,
 * please don't have any side effects in the class initialisation/instantiation,
 * as any module can be disabled via the Catalyx config, or by its dependencies being unmet.
 */
interface ICatalyxModule {
	/**
	 * A logger to use for this module.
	 */
	val logger: Logger

	/**
	 * A boolean indicating whether this module is enabled.
	 */
	val enabled: Boolean
		get() = moduleManager.isModuleEnabled(this)

	/**
	 * Called when this module is loaded.
	 */
	fun load() = Unit

	/**
	 * Called before each of the other callbacks, but after the mod itself receives the event
	 */
	fun lifecycle(event: FMLStateEvent) = Unit

	fun construction(event: FMLConstructionEvent) = Unit

	fun preInit(event: FMLPreInitializationEvent) = Unit

	fun init(event: FMLInitializationEvent) = Unit

	fun postInit(event: FMLPostInitializationEvent) = Unit

	fun loadComplete(event: FMLLoadCompleteEvent) = Unit

	fun serverAboutToStart(event: FMLServerAboutToStartEvent) = Unit

	fun serverStarting(event: FMLServerStartingEvent) = Unit

	fun serverStarted(event: FMLServerStartedEvent) = Unit

	fun serverStopping(event: FMLServerStoppingEvent) = Unit

	fun serverStopped(event: FMLServerStoppedEvent) = Unit

	/**
	 * A list of classes to subscribe to the [Forge Event Bus][net.minecraftforge.common.MinecraftForge.EVENT_BUS].
	 *
	 * Like with registering yourself to the bus, you can pass a Class<*> for static functions, or a class instance for non-static functions.
	 */
	val eventBusSubscribers: Iterable<Any>
		get() = emptyList()

	/**
	 * A list of classes to subscribe to the [Forge Terrain Gen Bus][net.minecraftforge.common.MinecraftForge.TERRAIN_GEN_BUS].
	 *
	 * Like with registering yourself to the bus, you can pass a Class<*> for static functions, or a class instance for non-static functions.
	 */
	val terrainGenBusSubscribers: Iterable<Any>
		get() = emptyList()

	/**
	 * A list of classes to subscribe to the [Forge Ore Gen Bus][net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS].
	 *
	 * Like with registering yourself to the bus, you can pass a Class<*> for static functions, or a class instance for non-static functions.
	 */
	val oreGenBusSubscribers: Iterable<Any>
		get() = emptyList()
}
