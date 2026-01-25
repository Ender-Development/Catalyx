package org.ender_development.catalyx.api.v1.utils.interfaces

/**
 * Utility object for checking the current environment - side (client or server) and deobfuscation.
 */
interface IEnvironmentUtils {
	val isClient: Boolean
	val isServer: Boolean
	val isDedicatedClient: Boolean
	val isDedicatedServer: Boolean

	/**
	 * Whether you're in a deobfuscated (dev) environment
	 * @see [CoreModManager:208][net.minecraftforge.fml.relauncher.CoreModManager]
	 */
	val isDeobfuscated: Boolean
}
