package io.enderdev.catalyx.network

import io.enderdev.catalyx.Catalyx
import io.enderdev.catalyx.client.button.AbstractButtonWrapper
import io.enderdev.catalyx.tiles.helper.IButtonTile
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ButtonPacket() : IMessage {
	private lateinit var blockPos: BlockPos
	private lateinit var wrapperClass: Class<out AbstractButtonWrapper>
	private var x = 0
	private var y = 0
	private var width = 0
	private var height = 0
	private lateinit var extraData: ByteBuf

	override fun fromBytes(buf: ByteBuf) {
		blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
		x = buf.readInt()
		y = buf.readInt()
		width = buf.readInt()
		height = buf.readInt()
		val className = buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString()
		if(!AbstractButtonWrapper.buttonWrappers.contains(className)) {
			Catalyx.logger.error("Received illegal class name '${className}' in ButtonPacket")
			// we'll crash from a lateinit not being initialised later anyways so might as well
			throw IllegalArgumentException()
		}
		val `class` = Class.forName(className)
		@Suppress("UNCHECKED_CAST")
		if(AbstractButtonWrapper::class.java.isAssignableFrom(`class`)) // this should be guaranteed but check just in case
			wrapperClass = `class` as Class<out AbstractButtonWrapper>
		extraData = buf.readBytes(buf.readInt())
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(blockPos.x)
		buf.writeInt(blockPos.y)
		buf.writeInt(blockPos.z)
		buf.writeInt(x)
		buf.writeInt(y)
		buf.writeInt(width)
		buf.writeInt(height)
		buf.writeInt(wrapperClass.name.length)
		buf.writeCharSequence(wrapperClass.name, Charsets.UTF_8)
		buf.writeInt(extraData.readableBytes())
		buf.writeBytes(extraData)
	}

	constructor(pos: BlockPos, wrapper: AbstractButtonWrapper) : this() {
		blockPos = pos
		wrapperClass = wrapper::class.java
		x = wrapper.x
		y = wrapper.y
		width = wrapper.width
		height = wrapper.height
		extraData = Unpooled.buffer()
		wrapper.writeExtraData(extraData)
	}

	class Handler : IMessageHandler<ButtonPacket, IMessage> {
		override fun onMessage(message: ButtonPacket, ctx: MessageContext): IMessage? {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
			return null
		}

		private fun handle(message: ButtonPacket, ctx: MessageContext) {
			val playerEntity = ctx.serverHandler.player
			val tile = playerEntity.world.getTileEntity(message.blockPos)

			if(tile is IButtonTile) {
				val instance = message.wrapperClass.getDeclaredConstructor(Int::class.java, Int::class.java, Int::class.java, Int::class.java).newInstance(message.x, message.y, message.width, message.height)
				instance.readExtraData(message.extraData)
				tile.handleButtonPress(instance)
			} else // Received a ButtonPacket for a BlockPos which doesn't have a TileEntity that extends IButtonTile ;p
				Catalyx.logger.error("Received a ButtonPacket for a block which doesn't have a tile entity that can handle button presses")
		}
	}
}
