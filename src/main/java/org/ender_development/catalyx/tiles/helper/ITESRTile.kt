package org.ender_development.catalyx.tiles.helper

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.client.tesr.AbstractTESRenderer

@SideOnly(Side.CLIENT)
interface ITESRTile {
	val renderers: Array<AbstractTESRenderer>
}
