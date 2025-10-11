package org.ender_development.catalyx.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import org.ender_development.catalyx.Reference

internal object PacketHandler {
	val channel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID)

	fun init() {
		channel.registerMessage(ButtonPacket.Handler::class.java, ButtonPacket::class.java, 0, Side.SERVER)
	}
}
