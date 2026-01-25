package org.ender_development.catalyx.core.utils

import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.FMLCommonHandler
import org.ender_development.catalyx.api.v1.utils.interfaces.IEnvironmentUtils

/**
 * Utility object for checking the current side (client or server).
 */
object EnvironmentUtils : IEnvironmentUtils {
	private val handler = FMLCommonHandler.instance()

	override val isClient = handler.effectiveSide.isClient
	override val isServer = handler.effectiveSide.isServer
	override val isDedicatedClient = handler.side.isClient
	override val isDedicatedServer = handler.side.isServer
	override val isDeobfuscated = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
}
