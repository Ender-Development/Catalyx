package org.ender_development.catalyx_.core.utils

import net.minecraftforge.fml.common.FMLCommonHandler

/**
 * Utility object for checking the current side (client or server).
 */
object SideUtils {
	private val handler = FMLCommonHandler.instance()

	val isClient = handler.effectiveSide.isClient
	val isServer = handler.effectiveSide.isServer
	val isDedicatedClient = handler.side.isClient
	val isDedicatedServer = handler.side.isServer
}
