package org.ender_development.catalyx.network

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.client.button.AbstractButtonWrapper
import org.ender_development.catalyx.tiles.helper.IButtonTile
import org.ender_development.catalyx.utils.extensions.readString
import org.ender_development.catalyx.utils.extensions.writeString

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
		val className = buf.readString()
		if(!AbstractButtonWrapper.buttonWrappers.contains(className)) {
			// this is needed to prevent potential security risks from people being able to send custom packets and potentially loading any class they want
			val error = "Received illegal class name '$className' in ButtonPacket"
			Catalyx.LOGGER.error(error)
			// we'll crash from a lateinit not being initialised later anyways so might as well
			throw IllegalArgumentException(error)
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
		buf.writeString(wrapperClass.name)
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
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask {
				handle(message, ctx)
			}
			return null
		}

		private fun handle(message: ButtonPacket, ctx: MessageContext) {
			val playerEntity = ctx.serverHandler.player
			val tile = playerEntity.world.getTileEntity(message.blockPos)

			if(tile !is IButtonTile) {
				// Received a ButtonPacket for a BlockPos which doesn't have a TileEntity that extends IButtonTile ;p
				Catalyx.LOGGER.error("Received a ButtonPacket for a block which doesn't have a tile entity that can handle button presses")
				return
			}
			val instance = message.wrapperClass.let {
				try {
					it.getDeclaredConstructor(Int::class.java, Int::class.java, Int::class.java, Int::class.java).newInstance(message.x, message.y, message.width, message.height)
				} catch(_: NoSuchMethodException) {
					try {
						it.getDeclaredConstructor(Int::class.java, Int::class.java).newInstance(message.x, message.y)
					} catch(_: NoSuchMethodException) {
						try {
							it.getDeclaredConstructor().newInstance()
						} catch(e: NoSuchMethodException) {
							Catalyx.LOGGER.error("No suitable constructor for class ${message.wrapperClass} found, tried (Int, Int, Int, Int)(x, y, w, h); (Int, Int)(x, y); ()")
							throw e
						}
					}
				}
			}
			instance.readExtraData(message.extraData, ctx)
			tile.handleButtonPress(instance)
		}
	}
}
