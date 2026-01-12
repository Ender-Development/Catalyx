package org.ender_development.catalyx.network

import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side
import org.ender_development.catalyx_.core.Reference
import org.ender_development.catalyx_.core.client.button.AbstractButtonWrapper

object PacketHandler {
	internal val channel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID)

	internal fun init() {
		channel.registerMessage(ButtonPacket.Handler::class.java, ButtonPacket::class.java, 0, Side.SERVER)
	}

	fun sendWrapper(pos: BlockPos, wrapper: AbstractButtonWrapper) =
		channel.sendToServer(ButtonPacket(pos, wrapper))
}
