package org.ender_development.catalyx.blocks

import net.minecraftforge.fml.client.registry.ClientRegistry
import org.ender_development.catalyx.client.tesr.TileRenderer
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.tiles.TesrTile
import org.ender_development.catalyx.utils.SideUtils

class TesrTileBlock(mod: ICatalyxMod, name: String, tileClass: Class<out TesrTile>, guiId: Int) : BaseRotatableTileBlock(mod, name, tileClass, guiId) {
	init {
		if (SideUtils.isClient) {
			ClientRegistry.bindTileEntitySpecialRenderer(tileClass, TileRenderer)
		}
	}
}
