package org.ender_development.catalyx.core.common.tileentities

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.api.v1.ICatalyxMod
import org.ender_development.catalyx.core.client.tesr.HudInfoRenderer
import org.ender_development.catalyx.core.client.tesr.IORenderer
import org.ender_development.catalyx.api.v1.common.tileentities.interfaces.IPortRenderer

abstract class IOTile(mod: ICatalyxMod): TesrTile(mod), IPortRenderer {
	@SideOnly(Side.CLIENT)
	override val renderers = arrayOf(IORenderer, HudInfoRenderer)
}
