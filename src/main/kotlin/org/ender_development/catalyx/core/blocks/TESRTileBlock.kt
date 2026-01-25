package org.ender_development.catalyx.core.blocks

import net.minecraftforge.fml.client.registry.ClientRegistry
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.client.tesr.TileRenderer
import org.ender_development.catalyx.core.tiles.TESRTile

/**
 * A rotatable block that has a TESR. Binds the [TileRenderer] to the tile entity on the client side.
 */
open class TESRTileBlock(mod: ICatalyxMod, name: String, tileClass: Class<out TESRTile>, guiId: Int) : BaseRotatableTileBlock(mod, name, tileClass, guiId) {
	init {
		if(Utils.environment.isClient)
			ClientRegistry.bindTileEntitySpecialRenderer(tileClass, TileRenderer)
	}
}
