package org.ender_development.catalyx.core.common.blocks.tile

import net.minecraftforge.fml.client.registry.ClientRegistry
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.api.v1.ICatalyxMod
import org.ender_development.catalyx.core.client.tesr.TileRenderer
import org.ender_development.catalyx.core.common.tileentities.TesrTile

/**
 * A rotatable block that has a TESR. Binds the [TileRenderer] to the tile entity on the client side.
 */
open class TesrTileBlock(mod: ICatalyxMod, name: String, tileClass: Class<out TesrTile>, guiId: Int) : HorizontalTileBlock(mod, name, tileClass, guiId) {
	init {
		if(Utils.environment.isClient)
			ClientRegistry.bindTileEntitySpecialRenderer(tileClass, TileRenderer)
	}
}
