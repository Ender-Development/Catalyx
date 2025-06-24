package io.enderdev.catalyx.utils

import net.minecraftforge.fml.common.FMLCommonHandler

/**
 * Utility object for checking the current side (client or server).
 */
object SideUtils {
	fun isClient(): Boolean {
		return FMLCommonHandler.instance().effectiveSide.isClient
	}

	fun isServer(): Boolean {
		return FMLCommonHandler.instance().effectiveSide.isServer
	}

	fun isDedicatedServer(): Boolean {
		return FMLCommonHandler.instance().side.isServer
	}
}
