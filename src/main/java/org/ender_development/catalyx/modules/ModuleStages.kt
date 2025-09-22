package org.ender_development.catalyx.modules

/**
 * Basically [net.minecraftforge.fml.common.LoaderState] but only for launch stages.
 * Also includes early module stages.
 */
enum class ModuleStage {
	C_SETUP,  // Initializing Module Containers
	M_SETUP,  // Initializing Modules
	CONSTRUCTION,  // MC Construction stage
	PRE_INIT,  // MC PreInitialization stage
	INIT,  // MC Initialization stage
	POST_INIT,  // MC PostInitialization stage
	FINISHED,  // MC LoadComplete stage
	SERVER_ABOUT_TO_START,  // MC ServerAboutToStart stage
	SERVER_STARTING,  // MC ServerStarting stage
	SERVER_STARTED // MC ServerStarted stage
}
