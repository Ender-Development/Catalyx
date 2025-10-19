package org.ender_development.catalyx.core

import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.client.AreaHighlighter

internal object CoreEventHandler {
	@JvmStatic // required because of EventBus shitfuckery
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	fun renderWorldLast(event: RenderWorldLastEvent) =
		AreaHighlighter.eventHandlers.toTypedArray().forEach { it(event) } // convert to array to avoid a ConcurrentME
}
