package io.enderdev.catalyx.utils

import net.minecraftforge.fml.common.FMLCommonHandler

/**
 * Utility object for checking the current side (client or server).
 */
object SideUtils {
	private val handler = FMLCommonHandler.instance()

	// TODO - do these ever change? if not, just set the variables instead of using a getter function
	val isClient: Boolean
		get() = handler.effectiveSide.isClient

	val isServer: Boolean
		get() = handler.effectiveSide.isServer

	val isDedicatedServer: Boolean
		get() = handler.side.isServer
}
