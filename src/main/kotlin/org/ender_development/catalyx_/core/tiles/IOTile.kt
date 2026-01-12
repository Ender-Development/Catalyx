package org.ender_development.catalyx_.core.tiles

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx_.core.client.tesr.HudInfoRenderer
import org.ender_development.catalyx_.core.client.tesr.IORenderer
import org.ender_development.catalyx_.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx_.core.tiles.helper.IPortRenderer

abstract class IOTile(mod: ICatalyxMod): TESRTile(mod), IPortRenderer {
	@SideOnly(Side.CLIENT)
	override val renderers = arrayOf(IORenderer, HudInfoRenderer)
}
