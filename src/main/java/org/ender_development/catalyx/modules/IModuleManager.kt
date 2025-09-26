package org.ender_development.catalyx.modules

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.*
import org.ender_development.catalyx.Reference

interface IModuleManager {
	fun isModuleEnabled(containerID: String, moduleID: String) =
		isModuleEnabled(ResourceLocation(containerID, moduleID))

	fun isModuleEnabled(moduleID: String) =
		isModuleEnabled(ResourceLocation(Reference.MODID, moduleID))

	fun isModuleEnabled(id: ResourceLocation): Boolean
	fun registerContainer(container: ICatalyxModuleContainer?)

	val loadedContainer: ICatalyxModuleContainer?
	val moduleStage: ModuleStage

	fun passedStage(stage: ModuleStage) =
		moduleStage.ordinal > stage.ordinal

	fun construction(event: FMLConstructionEvent)
	fun preInit(event: FMLPreInitializationEvent)
	fun init(event: FMLInitializationEvent)
	fun postInit(event: FMLPostInitializationEvent)
	fun loadComplete(event: FMLLoadCompleteEvent)
	fun serverAboutToStart(event: FMLServerAboutToStartEvent)
	fun serverStarting(event: FMLServerStartingEvent)
	fun serverStarted(event: FMLServerStartedEvent)
	fun serverStopping(event: FMLServerStoppingEvent)
	fun serverStopped(event: FMLServerStoppedEvent)
}
