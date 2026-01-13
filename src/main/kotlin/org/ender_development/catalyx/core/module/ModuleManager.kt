package org.ender_development.catalyx.core.module

import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModClassLoader
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.*
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.annotations.module.CatalyxModule
import org.ender_development.catalyx.api.v1.annotations.module.CatalyxModuleContainer
import org.ender_development.catalyx.api.v1.interfaces.module.ICatalyxModule
import org.ender_development.catalyx.api.v1.interfaces.module.IModuleIdentifier
import org.ender_development.catalyx.api.v1.interfaces.module.IModuleManager
import org.ender_development.catalyx.api.v1.newModuleIdentifier
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.module.ModuleManager.configuration
import org.ender_development.catalyx.core.module.ModuleManager.discoveredContainers
import org.ender_development.catalyx.core.module.ModuleManager.discoveredModules
import org.ender_development.catalyx.core.module.ModuleManager.stateEvent
import org.ender_development.catalyx.core.utils.Delegates
import org.ender_development.catalyx.core.utils.DevUtils
import org.ender_development.catalyx.core.utils.extensions.modLoaded
import java.io.File
import java.util.*

private typealias ModId = String
private typealias ContainerId = String
private typealias ModuleId = String

object ModuleManager : IModuleManager {
	const val MODULE_CFG_CATEGORY_NAME = "modules"
	const val MODULE_CFG_FILE_NAME = "$MODULE_CFG_CATEGORY_NAME.cfg"

	private val loadedModules = ReferenceLinkedOpenHashSet<ICatalyxModule>()
	private val loadedModuleIds = hashSetOf<IModuleIdentifier>() // TODO: turn this into some fastutil bs
	private val loadedContainers = Object2ReferenceLinkedOpenHashMap<ContainerId, Any>()

	private val configDirectory = File(Loader.instance().configDir, Reference.MODID)

	/**
	 * The currently active Module Container
	 */
	override var activeContainer: Any? = null
		private set

	/**
	 * The configuration for the Module Manager
	 */
	private val configuration by Delegates.lazyProperty {
		Configuration(File(configDirectory, MODULE_CFG_FILE_NAME)).apply {
			load()
			addCustomCategoryComment(MODULE_CFG_CATEGORY_NAME, "Module configuration file. Can individually enable/disable modules from Catalyx and its addons.")
		}
	}

	// --- Main logic ---

	/**
	 * Set up the Module Manager
	 *
	 * Called during Catalyx construction
	 *
	 * @param asmDataTable the data table containing all the Module Container and Module classes
	 */
	internal fun setup(asmDataTable: ASMDataTable) {
		discoverContainers(asmDataTable)
		discoverModules(asmDataTable)
	}

	private val discoveredContainers = hashMapOf<ModId, MutableList<ASMDataTable.ASMData>>()

	/**
	 * Discovers [ModuleContainers][org.ender_development.catalyx.api.v1.annotations.module.CatalyxModuleContainer] for registration after mod construction
	 *
	 * @see [discoveredContainers]
	 * @param asmDataTable the table containing the ModuleContainer data
	 */
	private fun discoverContainers(asmDataTable: ASMDataTable) {
		Catalyx.LOGGER.debug("Discovering Module Containers...")
		asmDataTable.getAll(CatalyxModuleContainer::class.java.canonicalName).forEach {
			discoveredContainers.getOrPut(it.annotationInfo["modId"] as String, ::mutableListOf).add(it)
		}
	}

	private val discoveredModules = hashMapOf<ContainerId, MutableList<ASMDataTable.ASMData>>()

	/**
	 * Discovers [Modules][org.ender_development.catalyx.api.v1.annotations.module.CatalyxModule] for registration after their container gets registered
	 *
	 * @see discoveredModules
	 * @param asmDataTable the ASM Data Table containing the module data
	 */
	private fun discoverModules(asmDataTable: ASMDataTable) {
		Catalyx.LOGGER.debug("Discovering Modules...")
		asmDataTable.getAll(CatalyxModule::class.java.canonicalName).forEach { asmModule ->
			val containerId = asmModule.annotationInfo["containerId"] as ContainerId
			val moduleId = asmModule.annotationInfo["moduleId"] as ModuleId
			val modDependencies = (asmModule.annotationInfo["modDependencies"] as List<*>?)?.filterIsInstance<String>() ?: run {
				Catalyx.LOGGER.debug("Module $moduleId is missing modDependencies annotation property. Assuming no mod dependencies.")
				emptyList()
			}

			if(!modDependencies.all(String::modLoaded)) {
				Catalyx.LOGGER.info("Module $moduleId is missing at least one of mod dependencies: ${modDependencies.joinToString(", ")}, skipping...")
				return@forEach
			}

			discoveredModules.getOrPut(containerId, ::mutableListOf).add(asmModule)
		}
	}

	override fun registerContainer(container: Any) {
		loadedContainers[container.containerAnnotation.id] = container
	}

	/**
	 * Register the modules according to the module [configuration]
	 *
	 * @param toRegister the modules to register
	 */
	private fun registerModules(toRegister: Map<ContainerId, MutableList<ICatalyxModule>>) {
		val locale = Locale.getDefault()
		Locale.setDefault(Locale.ENGLISH)

		val willLoadIds = ObjectLinkedOpenHashSet<ModuleIdentifier>()
		val willLoadModules = ReferenceLinkedOpenHashSet<ICatalyxModule>()

		toRegister.forEach { (containerId, modules) ->
			// Ensure core module exists and is first
			modules.indexOfFirst { it.annotation.coreModule }.let { idx ->
				if(idx == -1)
					error("Could not find core module for container $containerId")

				if(idx != 0)
					modules.add(0, modules.removeAt(idx))
			}

			// Add all but disabled modules to the load lists
			modules.forEach { module ->
				if(!shouldModuleBeEnabled(module)) {
					module.logger.info("Module ${module.moduleId} is disabled in config, skipping...")
					return@forEach
				}

				willLoadIds.add(ModuleIdentifier(containerId, module.moduleId))
				willLoadModules.add(module)
			}
		}

		// todo sort modules by dependencies to instantiate and call [load()] on those first lol

		loadedModules.addAll(willLoadModules)
		loadedModuleIds.addAll(willLoadIds)

		// Register event bus listeners
		willLoadModules.forEach { module ->
			activeContainer = loadedContainers[module.containerId]

			modContainerContext(activeContainer!!.containerAnnotation.modId) {
				module.load()

				module.eventBusSubscribers.forEach {
					module.logger.debug("Registered event handler {} ({})", it, it::class.java.canonicalName)
					MinecraftForge.EVENT_BUS.register(it)
				}

				module.oreGenBusSubscribers.forEach {
					module.logger.debug("Registered ore gen event handler {} ({})", it, it::class.java.canonicalName)
					MinecraftForge.ORE_GEN_BUS.register(it)
				}

				module.terrainGenBusSubscribers.forEach {
					module.logger.debug("Registered terrain gen event handler {} ({})", it, it::class.java.canonicalName)
					MinecraftForge.TERRAIN_GEN_BUS.register(it)
				}
			}

			activeContainer = null
		}

		if(configuration.hasChanged())
			configuration.save()

		Locale.setDefault(locale)
	}

	/**
	 * Called by [LoadController#sendEventToModContainer][org.ender_development.catalyx.mixin.LoadControllerMixin.sendEventToModContainer]
	 */
	internal fun stateEvent(mod: ModContainer, stateEvent: FMLStateEvent) {
		// After a mod's construction, find and register any containers and modules
		discoveredContainers.remove(mod.modId)?.let { discoveredContainers ->
			if(discoveredContainers.isEmpty())
				return@let

			if(stateEvent !is FMLConstructionEvent)
				error("Somehow we still found discovered module containers for mod ${mod.modId} during ${stateEvent.eventType}, containers: ${discoveredContainers.joinToString(", ", transform = { it.className })}. This shouldn't happen.")

			Catalyx.LOGGER.debug("Instantiating modules for mod ${mod.modId}")

			instantiateNewContainers(discoveredContainers, stateEvent.modClassLoader, mod)

			Catalyx.LOGGER.debug("Finished instantiating modules for mod ${mod.modId}")
		}

		// Call the corresponding state function for each module in each container for the given mod
		// note: iterating like this here sucks
		val stateName = stateEvent::class.java.simpleName.removeSurrounding("FML", "Event").replace("[A-Z]".toRegex()) { " ${it.value}" }.trimStart()

		loadedContainers.values.forEach { container ->
			if(container.containerAnnotation.modId != mod.modId)
				return@forEach

			activeContainer = container

			loadedModules.forEach { module ->
				if(module.containerId != container.containerAnnotation.id)
					return@forEach

				module.logger.debug("Starting $stateName stage")
				module.lifecycle(stateEvent)
				when(stateEvent) {
					is FMLConstructionEvent -> module.construction(stateEvent)
					is FMLPreInitializationEvent -> module.preInit(stateEvent)
					is FMLInitializationEvent -> module.init(stateEvent)
					is FMLPostInitializationEvent -> module.postInit(stateEvent)
					is FMLLoadCompleteEvent -> module.loadComplete(stateEvent)
					is FMLServerAboutToStartEvent -> module.serverAboutToStart(stateEvent)
					is FMLServerStartingEvent -> module.serverStarting(stateEvent)
					is FMLServerStartedEvent -> module.serverStarted(stateEvent)
					is FMLServerStoppingEvent -> module.serverStopping(stateEvent)
					is FMLServerStoppedEvent -> module.serverStopped(stateEvent)
				}
				module.logger.debug("Completed $stateName stage")
			}
		}

		activeContainer = null
	}

	/**
	 * Instantiates new containers and their modules.
	 *
	 * Helper function for [stateEvent] so it's less of a mess.
	 */
	private fun instantiateNewContainers(discoveredContainers: MutableList<ASMDataTable.ASMData>, loader: ModClassLoader, mod: ModContainer) {
		val newContainers = hashMapOf<ContainerId, MutableList<ICatalyxModule>>()

		val modId = mod.modId
		modContainerContext(modId) {
			discoveredContainers.forEach { asmContainer ->
				Catalyx.LOGGER.debug("> Instantiating Module Container {}:{}", modId, asmContainer.className)

				val container = loadClassAndCreateInstance<Any>(loader, asmContainer, "Module Container") ?: return@forEach
				val containerId = container.containerAnnotation.id

				registerContainer(container)
				activeContainer = container

				Catalyx.LOGGER.debug("> Module Container {}:{} instantiated ({})", modId, containerId, asmContainer.className)

				var discoveredModules = discoveredModules.remove(containerId).orEmpty()
				if(discoveredModules.isEmpty()) {
					Catalyx.LOGGER.warn("> Module Container {}:{} has no modules", modId, containerId)
					return@forEach
				}

				// Check module ids before instantiating modules
				val willInstantiate = mutableListOf<ASMDataTable.ASMData>()
				val willInstantiateIds = mutableListOf<IModuleIdentifier>()
				do {
					var changed = false
					discoveredModules = discoveredModules.filter { module ->
						val moduleId = newModuleIdentifier(module.annotationInfo["containerId"] as String, module.annotationInfo["moduleId"] as String)
						val unmetDependencies = (module.annotationInfo["moduleDependencies"] as List<*>? ?: emptyList<String>())
							.filterIsInstance<String>()
							.map(ResourceLocation::splitObjectName)
							.map{ newModuleIdentifier(it[0], it[1]) }
							.filterNot { willInstantiateIds.contains(it) || loadedModuleIds.contains(it) }

						return@filter if(unmetDependencies.isEmpty()) {
							changed = true
							willInstantiate.add(module)
							willInstantiateIds.add(moduleId)
							false
						} else true
					}.toMutableList()
				} while(changed)

				Catalyx.LOGGER.debug("> Module Container {}:{} has {} dependent modules, but will be instantiating {} of them", modId, containerId, willInstantiate.size + discoveredModules.size, willInstantiate.size)
				willInstantiate.indexOfFirst { it.annotationInfo["coreModule"] as Boolean? ?: false }.let { idx ->
					if(idx != -1 && idx != 0)
						willInstantiate.add(0, willInstantiate.removeAt(idx))
				}

				val modules = mutableListOf<ICatalyxModule>()

				willInstantiate.forEach { asmModule ->
					Catalyx.LOGGER.debug("Instantiating Module {}:{}:{} ({})", modId, containerId, asmModule.annotationInfo["moduleId"], asmModule.className)

					modules.add(loadClassAndCreateInstance<ICatalyxModule>(loader, asmModule, "Module") ?: return@forEach)

					Catalyx.LOGGER.debug("Module {}:{}:{} instantiated ({})", modId, containerId, asmModule.annotationInfo["moduleId"], asmModule.className)
				}

				newContainers[containerId] = modules
			}

			registerModules(newContainers)
		}

		activeContainer = null
	}

	// --- Misc. helper methods ---

	/**
	 * Helper function to load a given [asm] class with a given [loader], check if it implements/extends [I] and return an instance, optionally erroring with a [type] message.
	 *
	 * @param I Interface/class that the given [asm] class needs to implement/extend
	 * @param loader The loader to load the class with
	 * @param asm The ASM class to load
	 * @param type What is being loaded - used for error messages
	 * @return An instance of [asm] cast to [I], or null if anything fails
	 */
	private inline fun <reified I> loadClassAndCreateInstance(loader: ModClassLoader, asm: ASMDataTable.ASMData, type: String): I? {
		try {
			val clazz = loader.loadClass(asm.className)

			if(!I::class.java.isAssignableFrom(clazz)) {
				Catalyx.LOGGER.error("$type Class ${asm.className} does not implement ${I::class.java.simpleName}")
				return null
			}

			val instance = (clazz.declaredFields.firstOrNull { it.name.equals("instance", true) }?.get(null) ?: clazz.getConstructor().newInstance()) as I?

			if(instance == null)
				Catalyx.LOGGER.error("$type Class ${asm.className} - couldn't find or create any instance")

			return instance
		} catch(e: Throwable) {
			when(e) {
				is ClassNotFoundException,
				is IllegalAccessException,
				is InstantiationException,
				is NoSuchMethodException -> {
					Catalyx.LOGGER.error("Couldn't instantiate $type Class ${asm.className}", e)
					return null
				}
				else -> throw e
			}
		}
	}

	/**
	 * @param identifier the id of the module to check
	 * @return true if the module is enabled, false otherwise
	 */
	override fun isModuleEnabled(identifier: IModuleIdentifier) =
		loadedModuleIds.contains(identifier)

	override fun isModuleEnabled(module: ICatalyxModule) =
		loadedModules.contains(module)

	@Suppress("NOTHING_TO_INLINE")
	internal inline fun isModuleEnabled(moduleId: String) =
		isModuleEnabled(ModuleIdentifier(Reference.MODID, moduleId))

	/**
	 * @param module the module to get the comment for
	 * @return the comment for the module's configuration
	 */
	private fun getConfigComment(module: ICatalyxModule): String {
		val annotation = module.annotation

		return buildString {
			appendLine(annotation.description)
			annotation.moduleDependencies.joinTo(this, separator = ", ", prefix = "Module Dependencies: [", postfix = "]\n")
			annotation.modDependencies.joinTo(this, separator = ", ", prefix = "Mod Dependencies: [", postfix = "]")
		}
	}

	private fun shouldModuleBeEnabled(module: ICatalyxModule): Boolean {
		val annotation = module.annotation
		val prop = configuration.get(MODULE_CFG_CATEGORY_NAME, "${annotation.containerId}:${annotation.moduleId}", true, getConfigComment(module))
		return prop.boolean && (!annotation.testModule || DevUtils.isDeobfuscated)
	}

	// --- Helper properties ---

	/**
	 * @return the [CatalyxModule] annotation for this [ICatalyxModule]
	 */
	private val ICatalyxModule.annotation
		inline get() = this::class.java.getAnnotation(CatalyxModule::class.java)

	/**
	 * @return the [CatalyxModuleContainer] annotation for a Catalyx Module Container (supposedly anyways)
	 */
	private val Any.containerAnnotation
		inline get() = this::class.java.getAnnotation(CatalyxModuleContainer::class.java)

	/**
	 * @return the container id
	 */
	private val ICatalyxModule.containerId: ContainerId
		inline get() = annotation.containerId

	/**
	 * @return the module id
	 */
	private val ICatalyxModule.moduleId: ModuleId
		inline get() = annotation.moduleId

	private inline fun modContainerContext(modId: String, crossinline function: () -> Unit) {
		val currentModContainer = Loader.instance().activeModContainer()
		Loader.instance().setActiveModContainer(Loader.instance().indexedModList[modId])
		function()
		Loader.instance().setActiveModContainer(currentModContainer)
	}
}
