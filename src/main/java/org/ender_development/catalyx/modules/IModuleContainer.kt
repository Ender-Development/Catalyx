package org.ender_development.catalyx.modules

interface IModuleContainer {
	/**
	 * The ID of this container. If this is your mod's only container, you should use your mod ID to prevent collisions.
	 */
	val id: String
}
