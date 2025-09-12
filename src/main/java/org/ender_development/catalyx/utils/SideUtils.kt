package org.ender_development.catalyx.utils

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.FMLCommonHandler

/**
 * Utility object for checking the current side (client or server).
 */
object SideUtils {
	private val handler = FMLCommonHandler.instance()

	val isClient: Boolean = handler.effectiveSide.isClient
	val isServer: Boolean = handler.effectiveSide.isServer
	val isDedicatedClient: Boolean = handler.side.isClient
	val isDedicatedServer: Boolean = handler.side.isServer

	fun isClient(player: EntityPlayer?): Boolean {
		if (player == null) throw NullPointerException("Can't get the side of a null player!")
		return if (player.world == null) player is EntityPlayerSP else player.world.isRemote
	}

	fun isServer(player: EntityPlayer?): Boolean {
		if (player == null) throw NullPointerException("Can't get the side of a null player!")
		return if (player.world == null) player is EntityPlayerMP else !player.world.isRemote
	}
}
