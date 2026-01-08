package org.ender_development.catalyx.modules

import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Logger

/**
 * All modules must implement this interface.
 *
 * Provides methods for responding to FML lifecycle events and adding event bus subscriber classes.
 */
interface ICatalyxModule {
	/**
	 * What other modules this module depends on, e.g. `ModuleIdentifier("catalyx", "foo_module")`
	 * represents a dependency on the module "foo_module" in the container "catalyx".
	 */
	val dependencyUids: Set<ModuleIdentifier>
		get() = emptySet()

	/**
	 * A logger to use for this module.
	 */
	val logger: Logger

	/**
	 * Called before each of the other callbacks, but after the mod itself receives the event
	 */
	fun lifecycle(event: FMLStateEvent) {}

	fun construction(event: FMLConstructionEvent) {}

	fun preInit(event: FMLPreInitializationEvent) {}

	fun init(event: FMLInitializationEvent) {}

	fun postInit(event: FMLPostInitializationEvent) {}

	fun loadComplete(event: FMLLoadCompleteEvent) {}

	fun serverAboutToStart(event: FMLServerAboutToStartEvent) {}

	fun serverStarting(event: FMLServerStartingEvent) {}

	fun serverStarted(event: FMLServerStartedEvent) {}

	fun serverStopping(event: FMLServerStoppingEvent) {}

	fun serverStopped(event: FMLServerStoppedEvent) {}

	/**
	 * A list of classes to subscribe to the Forge Event Bus.
	 * The class itself gets subscribed, instead of a class instance,
	 * so event handlers **must** be `static`.
	 *
	 * @see net.minecraftforge.common.MinecraftForge.EVENT_BUS
	 */
	val eventBusSubscribers: List<Class<*>>
		get() = emptyList()

	/**
	 * A list of classes to subscribe to the Forge Terrain Gen Bus.
	 * The class itself gets subscribed, instead of a class instance,
	 * so event handlers **must** be `static`.
	 *
	 * @see net.minecraftforge.common.MinecraftForge.TERRAIN_GEN_BUS
	 */
	val terrainGenBusSubscriber: List<Class<*>>
		get() = emptyList()

	/**
	 * A list of classes to subscribe to the Forge Ore Gen Bus.
	 * The class itself gets subscribed, instead of a class instance,
	 * so event handlers **must** be `static`.
	 *
	 * @see net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS
	 */
	val oreGenBusSubscriber: List<Class<*>>
		get() = emptyList()
}
