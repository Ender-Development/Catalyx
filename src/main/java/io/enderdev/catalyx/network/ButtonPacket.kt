package io.enderdev.catalyx.network

import io.enderdev.catalyx.Catalyx
import io.enderdev.catalyx.tiles.helper.IButtonTile
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ButtonPacket() : IMessage {
	private lateinit var blockPos: BlockPos
	private var id = 0

	override fun fromBytes(buf: ByteBuf) {
		this.blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
		this.id = buf.readInt()
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(blockPos.x)
		buf.writeInt(blockPos.y)
		buf.writeInt(blockPos.z)
		buf.writeInt(id)
	}

	constructor(pos: BlockPos, id: Int) : this() {
		this.blockPos = pos
		this.id = id
	}

	class Handler : IMessageHandler<ButtonPacket, IMessage> {
		override fun onMessage(message: ButtonPacket, ctx: MessageContext): IMessage? {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
			return null
		}

		private fun handle(message: ButtonPacket, ctx: MessageContext) {
			val playerEntity = ctx.serverHandler.player
			val tile = playerEntity.world.getTileEntity(message.blockPos)

			if(tile is IButtonTile)
				tile.handleButtonPress(message.id)
			else // Received a ButtonPacket for a BlockPos which doesn't have a TileEntity that extends IButtonTile ;p
				Catalyx.logger.error("Received a ButtonPacket for a block which doesn't have a tile entity that can handle button presses")

			// TODO: rewrite
			//if(tile is AbstractMachine<*> && message.pause) {
			//	tile.isPaused = !(tile.isPaused)
			//}
			//if(tile is AbstractMachine<*> && message.redstone) {
			//	tile.needsPower = !(tile.needsPower)
			//}
			//if(tile is TileChemicalCombiner && message.lock) {
			//	tile.recipeIsLocked = !(tile.recipeIsLocked)
			//	if(!tile.recipeIsLocked) tile.currentRecipe = null
			//}
			//if(tile is TileFusionController && message.single) {
			//	tile.singleMode = !(tile.singleMode)
			//}
		}
	}
}
