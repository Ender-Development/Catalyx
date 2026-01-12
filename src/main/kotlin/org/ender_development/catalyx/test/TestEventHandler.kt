package org.ender_development.catalyx.test

import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx_.core.client.AreaHighlighter

internal object TestEventHandler {
	val areaHighlighter = AreaHighlighter()

	@SubscribeEvent
	@JvmStatic
	fun onChat(ev: ClientChatEvent) {
		if(!ev.message.startsWith($$"$c.h "))
			return

		val split = ev.message.removePrefix($$"$c.h ").split(" ")
		try {
			if(split[0][0] == 'T') {
				areaHighlighter.thickness = split[0].substring(1).toFloat()
				return
			}
			val x1 = split[0].toDouble()
			val y1 = split[1].toDouble()
			val z1 = split[2].toDouble()
			val x2 = split[3].toDouble()
			val y2 = split[4].toDouble()
			val z2 = split[5].toDouble()
			val r = split[6].toFloat()
			val g = split[7].toFloat()
			val b = split[8].toFloat()
			val time = split[9].toInt()
			areaHighlighter.highlightArea(x1, y1, z1, x2, y2, z2, r, g, b, time)
		} catch(e: Exception) {
			Minecraft.getMinecraft().player.sendMessage(TextComponentString($$"usage: $c.h x1 y1 z1 z2 y2 z2 r g b time\nor: usage: $c.h T<thickness>\ngot: $$e"))
			e.printStackTrace()
		}
	}
}
