package org.ender_development.catalyx.modules

/**
 * Basically [net.minecraftforge.fml.common.LoaderState] but only for launch stages.
 * Also includes early module stages.
 */
enum class ModuleStage {
	CONTAINER_SETUP("Container Setup"),  // Initializing Module Containers
	MODULE_SETUP("Module Setup"),  // Initializing Modules
	CONSTRUCTION("Construction"),  // MC Construction stage
	PRE_INIT("Pre-Initialization"),  // MC PreInitialization stage
	INIT("Initialization"),  // MC Initialization stage
	POST_INIT("Post-Initialization"),  // MC PostInitialization stage
	FINISHED("Finished"),  // MC LoadComplete stage
	SERVER_ABOUT_TO_START("Server about to start"),  // MC ServerAboutToStart stage
	SERVER_STARTING("Server starting"),  // MC ServerStarting stage
	SERVER_STARTED("Server started"), // MC ServerStarted stage
	SERVER_STOPPING("Server stopping"),  // MC ServerStopping stage
	SERVER_STOPPED("Server stopped");  // MC ServerStopped stage

	val displayName: String

	constructor(displayName: String) { this.displayName = displayName }

	override fun toString() = displayName
}
