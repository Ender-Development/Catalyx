package org.ender_development.catalyx_.core.client.tesr

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.ITESRTile

@SideOnly(Side.CLIENT)
object TileRenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		if(tileEntity !is ITESRTile)
			return

		tileEntity.renderers.forEach {
			it.setRendererDispatcher(rendererDispatcher)
			it.render(tileEntity, x, y, z, partialTicks, destroyStage, alpha)
		}
	}

	override fun isGlobalRenderer(tileEntity: BaseTile) =
		(tileEntity as? ITESRTile)?.renderers?.any { renderer -> renderer.isGlobalRenderer(tileEntity) } == true
}
