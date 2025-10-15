package org.ender_development.catalyx.modules

import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModClassLoader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.*
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.Delegates
import org.ender_development.catalyx.utils.DevUtils
import org.ender_development.catalyx.utils.extensions.modLoaded
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.*

object ModuleManager : IModuleManager {
	const val MODULE_CFG_CATEGORY_NAME = "modules"
	const val MODULE_CFG_FILE_NAME = "$MODULE_CFG_CATEGORY_NAME.cfg"

	private val sortedModules = Object2ReferenceLinkedOpenHashMap<ResourceLocation, ICatalyxModule>()
	private val loadedModules = ReferenceLinkedOpenHashSet<ICatalyxModule>()
	private val containers = Object2ReferenceLinkedOpenHashMap<String, ICatalyxModuleContainer>()

	private lateinit var configDirectory: File

	/**
	 * The currently loaded Module Container
	 */
	override var loadedContainer: ICatalyxModuleContainer? = null
		private set

	/**
	 * The current stage of the Module loading process
	 */
	override var moduleStage: ModuleStage = ModuleStage.CONTAINER_SETUP
		private set

	/**
	 * The configuration for the Module Manager
	 */
	private val configuration: Configuration by Delegates.lazyProperty { Configuration(File(configDirectory, MODULE_CFG_FILE_NAME)) }

	/**
	 * Set up the Module Manager
	 *
	 * @param asmDataTable the data table containing all the Module Container and Module classes
	 */
	fun setup(asmDataTable: ASMDataTable, loader: ModClassLoader) {
		discoverContainers(asmDataTable, loader)

		moduleStage = ModuleStage.MODULE_SETUP
		configDirectory = File(Loader.instance().configDir, Reference.MODID)
		configureModules(getModules(asmDataTable, loader))

		loadedModules.forEach { module ->
			loadedContainer = containers[module.containerID]
			module.eventBusSubscribers.forEach {
				module.logger.debug("Registered event handler ${it.canonicalName}")
				MinecraftForge.EVENT_BUS.register(it)
			}
			module.oreGenBusSubscriber.forEach {
				module.logger.debug("Registered ore gen event handler ${it.canonicalName}")
				MinecraftForge.ORE_GEN_BUS.register(it)
			}
			module.terrainGenBusSubscriber.forEach {
				module.logger.debug("Registered terrain gen event handler ${it.canonicalName}")
				MinecraftForge.TERRAIN_GEN_BUS.register(it)
			}
		}
		loadedContainer = null
	}

	/**
	 * @param id the ID of the module to check
	 * @return true if the module is enabled, false otherwise
	 */
	override fun isModuleEnabled(id: ResourceLocation) =
		sortedModules.containsKey(id)

	/**
	 * @param modules the list of modules possibly containing a Core Module
	 * @return the first found Core Module found
	 */
	private fun getCoreModule(modules: Iterable<ICatalyxModule>): ICatalyxModule? =
		modules.firstOrNull { it::class.java.getAnnotation(CatalyxModule::class.java).coreModule }

	/**
	 * @return the [CatalyxModule] annotation for this [ICatalyxModule]
	 */
	private val ICatalyxModule.annotation
		inline get() = this::class.java.getAnnotation(CatalyxModule::class.java)

	/**
	 * @return the container ID
	 */
	private val ICatalyxModule.containerID: String
		inline get() = annotation.containerID

	/**
	 * @return the module ID
	 */
	private val ICatalyxModule.moduleID: String
		inline get() = annotation.moduleID

	/**
	 * @param asmDataTable the ASM Data Table containing the module data
	 * @return all ICatalyxModule instances in sorted order by Container and Module ID
	 */
	private fun getInstances(asmDataTable: ASMDataTable, loader: ModClassLoader): List<ICatalyxModule> {
		val instances = mutableListOf<ICatalyxModule>()
		asmDataTable.getAll(CatalyxModule::class.java.canonicalName).forEach {
			val moduleID = it.annotationInfo["moduleID"] as String
			val modDependencies = it.annotationInfo["modDependencies"]
				?.let { dependencies -> (dependencies as List<*>).filterIsInstance<String>() }
				?: run {
					Catalyx.LOGGER.debug("Module $moduleID is missing modDependencies annotation property. Assuming no mod dependencies.")
					emptyList()
				}

			if(modDependencies.all(String::modLoaded)) {
				try {
					val clazz = loader.loadClass(it.className)
					if(ICatalyxModule::class.java.isAssignableFrom(clazz))
						instances.add(clazz.getConstructor().newInstance() as ICatalyxModule)
					else
						Catalyx.LOGGER.error("Module of class ${it.className} with id $moduleID is not an instance of ICatalyxModule")
				} catch(e: Exception) {
					when(e) {
						is ClassNotFoundException,
						is IllegalAccessException,
						is InstantiationException,
						is NoSuchMethodException,
						is InvocationTargetException -> Catalyx.LOGGER.error("Could not initialize module $moduleID", e)
						else -> throw e
					}
				}
			} else
				Catalyx.LOGGER.info("Module $moduleID is missing at least one of mod dependencies: ${modDependencies.joinToString(", ")}. Skipping...")
		}
		return instances.sortedWith(compareBy({ it.containerID }, { it.moduleID }))
	}

	/**
	 * @param asmDataTable the ASM Data Table containing the module data
	 * @return a map of Container ID to list of associated modules sorted by Module ID
	 */
	private fun getModules(asmDataTable: ASMDataTable, loader: ModClassLoader): Map<String, MutableList<ICatalyxModule>> {
		val modules = Object2ReferenceLinkedOpenHashMap<String, MutableList<ICatalyxModule>>()
		getInstances(asmDataTable, loader).forEach {
			modules.computeIfAbsent(it.containerID) { _ -> mutableListOf() }.add(it)
		}
		return modules
	}

	/**
	 * @param module the module to get the comment for
	 * @return the comment for the module's configuration
	 */
	private fun getComment(module: ICatalyxModule): String {
		val annotation = module.annotation
		val dependencies = module.dependencyUids
		var comment = annotation.description
		if(!dependencies.isEmpty())
			comment += dependencies.joinToString(", ", "\nModule Dependencies: [ ", " ]")

		val modDependencies = annotation.modDependencies
		if(!modDependencies.isEmpty())
			comment += modDependencies.joinToString(", ", "\nMod Dependencies: [ ", " ]")

		return comment
	}

	/**
	 * Discovers ModuleContainers and registers them
	 *
	 * @param asmDataTable the table containing the ModuleContainer data
	 */
	private fun discoverContainers(asmDataTable: ASMDataTable, loader: ModClassLoader) {
		Catalyx.LOGGER.debug("Discovering Module Containers...")
		asmDataTable.getAll(CatalyxModuleContainer::class.java.canonicalName).forEach {
			try {
				addModFileToClassLoader(it)
				val clazz = loader.loadClass(it.className)
				if(ICatalyxModuleContainer::class.java.isAssignableFrom(clazz)) {
					val container = if(Modifier.isFinal(clazz.modifiers)) {
						Catalyx.LOGGER.debug("Found final Module Container Class ${it.className}! Using INSTANCE field")
						clazz.getField("INSTANCE").get(null)
					} else {
						Catalyx.LOGGER.debug("Found non-final Module Container Class ${it.className}! Using default constructor")
						clazz.getConstructor().newInstance()
					}
					registerContainer(container as ICatalyxModuleContainer?)
				} else
					Catalyx.LOGGER.error("Module Container Class ${it.className} is not an instance of IModuleContainer")
			} catch(e: Exception) {
				when(e) {
					is ClassNotFoundException,
					is IllegalAccessException,
					is InstantiationException,
					is NoSuchMethodException -> Catalyx.LOGGER.error("Could not initialize Module Container ${it.className}", e)
					else -> throw e
				}
			}
		}
	}

	/**
	 * Because Catalyx construction is before every dependent mod, the [net.minecraft.launchwrapper.LaunchClassLoader]'s source list doesn't have any dependent mod's file added to it, and as such,
	 * trying to load it will just result in a [ClassNotFoundException]
	 *
	 * We only have to really deal with this during the Module Container phase, as containers are loaded and initalised before searching for modules.
	 *
	 * @param containerData ASM container data
	 */
	private fun addModFileToClassLoader(containerData: ASMDataTable.ASMData) {
		val modId = containerData.annotationInfo["modId"] as? String ?: error("Mod Container ${containerData.className} has no modId defined, somehow.") // error shouldn't happen
		val modContainer = Loader.instance().modList.firstOrNull { it.modId == modId } ?: error("Mod Container ${containerData.className} has an invalid modId of '$modId' => couldn't find a valid ModContainer to match")
		val url = modContainer.source.toURI().toURL()
		if(Launch.classLoader.sources.none { it == url })
			Launch.classLoader.addURL(url)

		// debug println for checking LCL sources
		// println(Launch.classLoader.sources.joinToString("',\n'", "\n'", "'", transform = URL::toString))
	}

	/**
	 * Configure the modules according to the module Configuration
	 *
	 * @param modules the modules to configure
	 */
	private fun configureModules(modules: Map<String, MutableList<ICatalyxModule>>) {
		val locale = Locale.getDefault()
		Locale.setDefault(Locale.ENGLISH)
		val toLoad = ObjectLinkedOpenHashSet<ResourceLocation>()
		val modulesToLoad = ReferenceLinkedOpenHashSet<ICatalyxModule>()
		val config = configuration
		config.load()
		config.addCustomCategoryComment(MODULE_CFG_CATEGORY_NAME, "Module configuration file. Can individually enable/disable modules from Catalyx and its addons.")
		containers.values.forEach { container ->
			val containerID = container.id
			val containerModules = modules[containerID] ?: throw IllegalStateException("Could not find any modules for container $containerID")

			getCoreModule(containerModules)?.let {
				containerModules.remove(it)
				containerModules.add(0, it) // Ensure core module is always first
			} ?: throw IllegalStateException("Could not find core module for container $containerID")

			val iterator = containerModules.iterator()
			while(iterator.hasNext()) {
				val module = iterator.next()
				if(!isModuleEnabled(module)) {
					module.logger.info("Module ${module.moduleID} is disabled in config, skipping...")
					iterator.remove()
					continue
				}
				toLoad.add(ResourceLocation(containerID, module.moduleID))
				modulesToLoad.add(module)
			}
		}

		// Check Module Dependencies
		var iterator: Iterator<ICatalyxModule>
		var changed: Boolean
		do {
			changed = false
			iterator = modulesToLoad.iterator()
			while(iterator.hasNext()) {
				val module = iterator.next()
				val dependencies = module.dependencyUids
				if(!toLoad.containsAll(dependencies)) {
					iterator.remove()
					changed = true
					val annotation = module.annotation
					toLoad.remove(ResourceLocation(annotation.containerID, annotation.moduleID))
					module.logger.info("Module ${annotation.moduleID} is missing at least one of its module dependencies: [ ${dependencies.joinToString(", ")} ]. Skipping...")
				}
			}
		} while(changed)

		// Sort modules by their module dependencies
		do {
			changed = false
			iterator = modulesToLoad.iterator()
			while(iterator.hasNext()) {
				val module = iterator.next()
				if(sortedModules.keys.containsAll(module.dependencyUids)) {
					iterator.remove()
					val annotation = module.annotation
					sortedModules[ResourceLocation(annotation.containerID, annotation.moduleID)] = module
					changed = true
					break
				}
			}
		} while(changed)

		loadedModules.addAll(sortedModules.values)

		if(config.hasChanged())
			config.save()

		Locale.setDefault(locale)
	}

	private fun isModuleEnabled(module: ICatalyxModule): Boolean {
		val annotation = module.annotation
		val comment = getComment(module)
		val prop = configuration.get(MODULE_CFG_CATEGORY_NAME, "${annotation.containerID}:${annotation.moduleID}", true, comment)
		return prop.boolean && (!annotation.testModule || DevUtils.isDeobfuscated)
	}

	override fun registerContainer(container: ICatalyxModuleContainer?) {
		when {
			container == null -> Catalyx.LOGGER.error("Failed to register null container!")
			moduleStage != ModuleStage.CONTAINER_SETUP -> Catalyx.LOGGER.error("Failed to register container ${container.id}, as module loading has already begun!")
			else -> containers[container.id] = container
		}
	}

	// FML Lifecycle Events

	private fun lifecycle(stage: ModuleStage, action: (ICatalyxModule, FMLStateEvent) -> Unit, event: FMLStateEvent) {
		moduleStage = stage
		loadedModules.forEach {
			val annotation = it.annotation
			loadedContainer = containers[annotation.containerID]
			it.logger.debug("Starting {} stage!", moduleStage)
			action(it, event)
			it.logger.debug("Completed {} stage!", moduleStage)
		}
		loadedContainer = null
	}

	override fun construction(event: FMLConstructionEvent) =
		lifecycle(ModuleStage.CONSTRUCTION, { module, _ -> module.construction(event) }, event)

	override fun preInit(event: FMLPreInitializationEvent) =
		lifecycle(ModuleStage.PRE_INIT, { module, _ -> module.preInit(event) }, event)

	override fun init(event: FMLInitializationEvent) =
		lifecycle(ModuleStage.INIT, { module, _ -> module.init(event) }, event)

	override fun postInit(event: FMLPostInitializationEvent) =
		lifecycle(ModuleStage.POST_INIT, { module, _ -> module.postInit(event) }, event)

	override fun loadComplete(event: FMLLoadCompleteEvent) =
		lifecycle(ModuleStage.FINISHED, { module, _ -> module.loadComplete(event) }, event)

	override fun serverAboutToStart(event: FMLServerAboutToStartEvent) =
		lifecycle(ModuleStage.SERVER_ABOUT_TO_START, { module, _ -> module.serverAboutToStart(event) }, event)

	override fun serverStarting(event: FMLServerStartingEvent) =
		lifecycle(ModuleStage.SERVER_STARTING, { module, _ -> module.serverStarting(event) }, event)

	override fun serverStarted(event: FMLServerStartedEvent) =
		lifecycle(ModuleStage.SERVER_STARTED, { module, _ -> module.serverStarted(event) }, event)

	override fun serverStopping(event: FMLServerStoppingEvent) =
		lifecycle(ModuleStage.SERVER_STOPPING, { module, _ -> module.serverStopping(event) }, event)

	override fun serverStopped(event: FMLServerStoppedEvent) =
		lifecycle(ModuleStage.SERVER_STOPPED, { module, _ -> module.serverStopped(event) }, event)
}
