package org.ender_development.catalyx.tiles

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.client.tesr.HudInfoRenderer
import org.ender_development.catalyx.client.tesr.IORenderer
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.tiles.helper.IPortRenderer

abstract class IOTile(mod: ICatalyxMod): TESRTile(mod), IPortRenderer {
	@SideOnly(Side.CLIENT)
	override val renderers = arrayOf(IORenderer, HudInfoRenderer)
}
