package org.ender_development.catalyx.utils

/**
 * Utility object for checking whether you're in a dev instance
 */
object DevUtils {
	// similar approach to FML's CoreModManager#L208 (but the variable they set is private-in-class)
	val isDeobfuscated = try {
		ClassLoader.getSystemClassLoader().loadClass("net.minecraft.world.World")
		true
	} catch(_: Exception) {
		false
	}
}
