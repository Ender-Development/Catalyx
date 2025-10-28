package org.ender_development.catalyx.client.tesr

import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.ITesr

object TileRenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		val te = (tileEntity as? ITesr) ?: return
		te.getRenderers().forEach {
			it.setRendererDispatcher(rendererDispatcher)
			it.render(tileEntity, x, y, z, partialTicks, destroyStage, alpha)
		}
	}

	override fun isGlobalRenderer(tileEntity: BaseTile): Boolean {
		return (tileEntity as? ITesr)?.let {
			it.getRenderers().any { renderer -> renderer.isGlobalRenderer(tileEntity) }
		} ?: false
	}
}
