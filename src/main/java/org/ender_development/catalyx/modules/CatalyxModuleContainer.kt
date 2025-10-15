package org.ender_development.catalyx.modules

/**
 * Annotate your [ICatalyxModuleContainer] with this for it to be automatically registered.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CatalyxModuleContainer(
	/**
	 * Your mod's id
	 */
	@Suppress("UNUSED") // used in ModuleManager via ASM
	val modId: String
)
