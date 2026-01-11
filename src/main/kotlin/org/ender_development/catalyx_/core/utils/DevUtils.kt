package org.ender_development.catalyx_.core.utils

import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.relauncher.CoreModManager

/**
 * Utility object for checking whether you're in a dev instance
 */
object DevUtils {
	/**
	 * Whether the environment is deobfuscated (dev environment)
	 * @see net.minecraftforge.fml.relauncher.CoreModManager#L208
	 * @return true if deobfuscated, false if obfuscated
	 */
	val isDeobfuscated: Boolean
		//inline get() = CoreModManager.deobfuscatedEnvironment
		inline get() = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
}
