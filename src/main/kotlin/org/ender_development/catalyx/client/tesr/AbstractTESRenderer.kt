package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import org.ender_development.catalyx.tiles.BaseTile

abstract class AbstractTESRenderer : TileEntitySpecialRenderer<BaseTile>() {
	companion object {
		const val TESR_MAGIC_NUMBER = 0.0075
		const val ONE_BLOCK_WIDTH = 1 / TESR_MAGIC_NUMBER
	}

	// Override super method to force use of BaseTile
	abstract override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)
}
