package org.ender_development.catalyx.core

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.blocks.multiblock.IMultiblockEdge
import org.ender_development.catalyx.blocks.multiblock.IMultiblockTile
import org.ender_development.catalyx.client.AreaHighlighter
import org.ender_development.catalyx.utils.persistence.WorldPersistentData

internal object CoreEventHandler {
	@JvmStatic // required because of EventBus shitfuckery
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) =
		AreaHighlighter.eventHandlers.toTypedArray().forEach { it(event) } // convert to array to avoid a ConcurrentME

	@JvmStatic
	@SubscribeEvent
	fun activateEdgeBlock(event: PlayerInteractEvent.RightClickBlock) {
		if (event.world.isRemote)
			return
		val blockState = event.world.getBlockState(event.pos)
		(blockState.block as? IMultiblockEdge).let {
			val posController = it?.getCenter(event.pos, blockState) ?: return
			val controller = event.world.getTileEntity(posController)
			if(controller !is IMultiblockTile)
				return Catalyx.LOGGER.error("Edge block at ${event.pos} pointed to invalid controller at $posController")

			val lookVector = event.entityPlayer.lookVec
			event.result = if(controller.activate(event.world, event.pos, blockState, event.entityPlayer, event.hand, event.face!!, lookVector.x, lookVector.y, lookVector.z))
				Event.Result.ALLOW
			else
				Event.Result.DENY
		}
	}

	fun serverAboutToStart(event: FMLServerAboutToStartEvent) =
		WorldPersistentData.instances.forEach(WorldPersistentData::worldJoined)

	fun serverStopped(event: FMLServerStoppedEvent) =
		WorldPersistentData.instances.forEach(WorldPersistentData::worldLeft)
}
