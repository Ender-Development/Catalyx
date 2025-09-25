package org.ender_development.catalyx.modules

import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.*
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.DevUtils
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.*

object ModuleManager : IModuleManager {
	var configDirectory: File? = null
	const val MODULE_CFG_FILE_NAME = "modules.cfg"
	const val MODULE_CFG_CATEGORY_NAME = "modules"

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
	private fun getInstances(asmDataTable: ASMDataTable): List<ICatalyxModule> {
		val instances = mutableListOf<ICatalyxModule>()
		asmDataTable.getAll(CatalyxModule::class.java.canonicalName).forEach {
			val moduleID = it.annotationInfo["moduleID"] as String
			val modDependencies = it.annotationInfo["modDependencies"]
				?.let { dependencies -> (dependencies as List<*>).filterIsInstance<String>() }
				?: run {
					Catalyx.LOGGER.debug("Module $moduleID is missing modDependencies annotation property. Assuming no mod dependencies.")
					emptyList()
				}

			if(modDependencies.all { dep -> Loader.isModLoaded(dep) }) {
				try {
					val clazz = Class.forName(it.className)
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
	private fun getModules(asmDataTable: ASMDataTable): Map<String, MutableList<ICatalyxModule>> {
		val modules = Object2ReferenceLinkedOpenHashMap<String, MutableList<ICatalyxModule>>()
		getInstances(asmDataTable).forEach {
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

	private var containers = Object2ReferenceLinkedOpenHashMap<String, IModuleContainer>()
	private val sortedModules = Object2ReferenceLinkedOpenHashMap<ResourceLocation, ICatalyxModule>()
	private val loadedModules = ReferenceLinkedOpenHashSet<ICatalyxModule>()

	private var config: Configuration? = null
	private var currentContainer: IModuleContainer? = null
	private var currentStage: ModuleStage = ModuleStage.CONTAINER_SETUP

	/**
	 * Set up the Module Manager
	 *
	 * @param asmDataTable    the data table containing all the Module Container and Module classes
	 */
	fun setup(asmDataTable: ASMDataTable) {
		discoverContainers(asmDataTable)
		// roz: hashmaps don't have any inherent order, why sort, and also why create a hashmap just to turn it into a fastutil hashmap? ;p
		containers = containers.entries
			.sortedBy { it.key }
			.associate { it.key to it.value }
			.let { Object2ReferenceLinkedOpenHashMap(it) }

		currentStage = ModuleStage.MODULE_SETUP
		configDirectory = File(Loader.instance().configDir, Reference.MODID)
		configureModules(getModules(asmDataTable))

		loadedModules.forEach { module ->
			currentContainer = containers[module.containerID]
			module.logger.debug("Registering event handlers")
			module.eventBusSubscribers.forEach(MinecraftForge.EVENT_BUS::register)
			module.terrainGenBusSubscriber.forEach(MinecraftForge.TERRAIN_GEN_BUS::register)
			module.oreGenBusSubscriber.forEach(MinecraftForge.ORE_GEN_BUS::register)
		}
		currentContainer = null
	}

	/**
	 * Discovers ModuleContainers and registers them
	 *
	 * @param asmDataTable the table containing the ModuleContainer data
	 */
	private fun discoverContainers(asmDataTable: ASMDataTable) {
		asmDataTable.getAll(ModuleContainer::class.java.canonicalName).forEach {
			try {
				val clazz = Class.forName(it.className)
				if(IModuleContainer::class.java.isAssignableFrom(clazz))
					registerContainer(clazz.getConstructor().newInstance() as IModuleContainer)
				else
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
					val moduleID = module.moduleID
					toLoad.remove(ResourceLocation(moduleID)) // roz: shouldn't this be ResourceLocation(containerID, moduleID)?
					module.logger.info("Module $moduleID is missing at least one of its module dependencies: [ ${dependencies.joinToString(", ")} ]. Skipping...")
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

	override fun isModuleEnabled(id: ResourceLocation) =
		sortedModules.containsKey(id)

	private fun isModuleEnabled(module: ICatalyxModule): Boolean {
		val annotation = module.annotation
		val comment = getComment(module)
		val prop = configuration.get(MODULE_CFG_CATEGORY_NAME, "${annotation.containerID}:${annotation.moduleID}", true, comment)
		return prop.boolean && (!annotation.testModule || DevUtils.isDeobfuscated)
	}

	val configuration: Configuration
		get() = config ?: Configuration(File(configDirectory, MODULE_CFG_FILE_NAME)).also { config = it }

	override val loadedContainer: IModuleContainer?
		get() = currentContainer

	override val moduleStage: ModuleStage
		get() = currentStage

	override fun registerContainer(container: IModuleContainer?) {
		when {
			container == null -> Catalyx.LOGGER.error("Failed to register null container!")
			currentStage != ModuleStage.CONTAINER_SETUP -> Catalyx.LOGGER.error("Failed to register container ${container.id}, as module loading has already begun!")
			else -> containers[container.id] = container
		}
	}

	// FML Lifecycle Events

	private fun lifecycle(stage: ModuleStage, action: (ICatalyxModule, FMLStateEvent) -> Unit, event: FMLStateEvent) {
		currentStage = stage
		loadedModules.forEach {
			val annotation = it.annotation
			currentContainer = containers[annotation.containerID]
			it.logger.debug("[${annotation.moduleID}] $currentStage start")
			action(it, event)
			it.logger.debug("[${annotation.moduleID}] $currentStage complete")
		}
		currentContainer = null
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
