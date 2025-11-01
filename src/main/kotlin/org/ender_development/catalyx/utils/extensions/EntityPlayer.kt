package org.ender_development.catalyx.utils.extensions

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP

val EntityPlayer.isClient
	get() = world?.isRemote ?: (this is EntityPlayerSP)

val EntityPlayer.isServer
	get() = world?.isRemote?.not() ?: (this is EntityPlayerMP)
