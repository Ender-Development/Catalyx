package org.ender_development.catalyx.tiles.helper

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx_.core.client.tesr.AbstractTESRenderer

@SideOnly(Side.CLIENT)
interface ITESRTile {
	/**
	 * The TESRs that will be called for rendering this TileEntity
	 *
	 * note: mark this as @SideOnly(Side.CLIENT), as the entire interface is marked as such.
	 */
	val renderers: Array<out AbstractTESRenderer>
}
