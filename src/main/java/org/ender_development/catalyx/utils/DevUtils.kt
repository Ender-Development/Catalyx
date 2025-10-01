package org.ender_development.catalyx.utils

/**
 * Utility object for checking whether you're in a dev instance
 */
object DevUtils {
	/**
	 * Whether the environment is deobfuscated (dev environment)
	 * @see net.minecraftforge.fml.relauncher.CoreModManager#L208
	 * @return true if deobfuscated, false if obfuscated
	 */
	val isDeobfuscated =
		try {
			ClassLoader.getSystemClassLoader().loadClass("net.minecraft.world.World")
			true
		} catch(_: Exception) {
			false
		}
}
