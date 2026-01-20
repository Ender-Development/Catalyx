package org.ender_development.catalyx.core.utils

import net.minecraftforge.fml.common.FMLCommonHandler
import org.ender_development.catalyx.api.v1.utils.interfaces.ISideUtils

/**
 * Utility object for checking the current side (client or server).
 */
object SideUtils : ISideUtils {
	private val handler = FMLCommonHandler.instance()

	override val isClient = handler.effectiveSide.isClient
	override val isServer = handler.effectiveSide.isServer
	override val isDedicatedClient = handler.side.isClient
	override val isDedicatedServer = handler.side.isServer
}
