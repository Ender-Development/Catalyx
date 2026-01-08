package org.ender_development.catalyx.modules

import org.ender_development.catalyx.Reference

/**
 * All of your [ICatalyxModule] classes must be annotated with this to be registered.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CatalyxModule(
	/**
	 * The id of this module. Must be unique within its container.
	 */
	val moduleId: String,

	/**
	 * The id of the container to associate this module with.
	 */
	val containerId: String,

	/**
	 * A human-readable name for this module.
	 */
	val name: String,

	/**
	 * A list of mod ids that this module depends on. If any mods specified are not present, the module will not load.
	 */
	val modDependencies: Array<String> = [],

	/**
	 * Whether this module is the "core" module for its container.
	 * Each container must have exactly one core module, which will be loaded before all other modules in the container.
	 *
	 * Core modules should not have mod dependencies.
	 */
	val coreModule: Boolean = false,

	/**
	 * Whether this module is a test module.
	 * Test modules will only be loaded if the game is running in a development environment.
	 * This is useful for modules that are only meant for testing and should not be included in production builds.
	 */
	val testModule: Boolean = false,

	/**
	 * The author of this module. Defaults to "Ender-Development".
	 */
	val author: String = Reference.AUTHOR,

	/**
	 * The version of this module. Defaults to the version of Catalyx.
	 */
	val version: String = Reference.VERSION,

	/**
	 * A description of this module in the module configuration file.
	 */
	val description: String = ""
)
