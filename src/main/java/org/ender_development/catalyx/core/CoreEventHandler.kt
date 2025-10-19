package org.ender_development.catalyx.core

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.blocks.multiblock.BaseEdge
import org.ender_development.catalyx.blocks.multiblock.IMultiBlockPart
import org.ender_development.catalyx.client.AreaHighlighter
import org.ender_development.catalyx.utils.extensions.getHorizontalCenterFromMeta

internal object CoreEventHandler {
	@JvmStatic // required because of EventBus shitfuckery
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) =
		AreaHighlighter.eventHandlers.toTypedArray().forEach { it(event) } // convert to array to avoid a ConcurrentME

	@JvmStatic
	@SubscribeEvent
	fun activateEdgeBlock(event: PlayerInteractEvent) {
		val blockState = event.world.getBlockState(event.pos)
		if(blockState.block !is BaseEdge)
			return

		val controller = event.world.getTileEntity(event.pos.getHorizontalCenterFromMeta(blockState.getValue(BaseEdge.state)))
		if(controller !is IMultiBlockPart)
			return

		val lookVector = event.entityPlayer.lookVec
		event.result = if(controller.activate(event.world, event.pos, blockState, event.entityPlayer, event.hand, event.face!!, lookVector.x, lookVector.y, lookVector.z))
			Event.Result.ALLOW
		else
			Event.Result.DENY
	}
}
