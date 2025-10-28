package org.ender_development.catalyx.tiles.helper

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.tiles.BaseTile

@SideOnly(Side.CLIENT)
interface ITesr {
	fun getRenderers(): MutableList<TileEntitySpecialRenderer<BaseTile>>
}
