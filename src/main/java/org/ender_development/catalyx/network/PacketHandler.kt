package org.ender_development.catalyx.network

import org.ender_development.catalyx.Reference
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object PacketHandler {
	lateinit var channel: SimpleNetworkWrapper

	internal fun init() {
		channel = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID)
		channel.registerMessage(ButtonPacket.Handler::class.java, ButtonPacket::class.java, 0, Side.SERVER)
	}
}
