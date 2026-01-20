package org.ender_development.catalyx.api.v1.utils.interfaces

/**
 * Utility object for checking the current side (client or server).
 */
interface ISideUtils {
	val isClient: Boolean
	val isServer: Boolean
	val isDedicatedClient: Boolean
	val isDedicatedServer: Boolean
}
