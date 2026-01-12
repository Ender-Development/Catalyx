package org.ender_development.catalyx_.core.blocks

import net.minecraftforge.fml.client.registry.ClientRegistry
import org.ender_development.catalyx_.core.client.tesr.TileRenderer
import org.ender_development.catalyx_.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx.tiles.TESRTile
import org.ender_development.catalyx_.core.utils.SideUtils

/**
 * A rotatable block that has a TESR. Binds the [TileRenderer] to the tile entity on the client side.
 */
open class TESRTileBlock(mod: ICatalyxMod, name: String, tileClass: Class<out TESRTile>, guiId: Int) : BaseRotatableTileBlock(mod, name, tileClass, guiId) {
	init {
		if(SideUtils.isClient)
			ClientRegistry.bindTileEntitySpecialRenderer(tileClass, TileRenderer)
	}
}
