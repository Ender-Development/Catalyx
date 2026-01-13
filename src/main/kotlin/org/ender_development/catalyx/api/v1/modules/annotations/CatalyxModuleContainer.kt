package org.ender_development.catalyx.api.v1.modules.annotations

/**
 * Annotate your Module Containers with this for it to be automatically registered.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CatalyxModuleContainer(
	/**
	 * Your mod's id
	 */
	val modId: String,

	/**
	 * The id of this container. If this is your mod's only container, you should use your mod id to prevent collisions.
	 */
	val id: String
)
