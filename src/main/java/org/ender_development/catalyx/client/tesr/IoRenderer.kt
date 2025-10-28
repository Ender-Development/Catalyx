package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.TesrTile
import org.ender_development.catalyx.tiles.helper.IPortRenderer
import org.ender_development.catalyx.utils.RenderUtils

object IoRenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		val provider = (tileEntity as? IPortRenderer) ?: return

		provider.getPortState().forEach { (side, state) ->
			val texture = when(state) {
				TesrTile.IOType.DEFAULT -> return@forEach
				TesrTile.IOType.NONE -> "catalyx:textures/blocks/io/none.png"
				TesrTile.IOType.INPUT -> "catalyx:textures/blocks/io/input.png"
				TesrTile.IOType.OUTPUT -> "catalyx:textures/blocks/io/output.png"
				TesrTile.IOType.PULL -> "catalyx:textures/blocks/io/pull.png"
				TesrTile.IOType.PUSH -> "catalyx:textures/blocks/io/push.png"
			}
			// TODO: I still need to figure out up/down facing rendering
			GlStateManager.pushMatrix()
			GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
			when(side) {
				EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
				EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
				EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
				//EnumFacing.UP -> GlStateManager.rotate(-90f, 1.0f, 0.0f, 0.0f)
				//EnumFacing.DOWN -> GlStateManager.rotate(90f, 1.0f, 0.0f, 0.0f)
				else -> {}
			}
			GlStateManager.translate(0.0, 0.0, 0.5)

			super.setLightmapDisabled(true)
			renderTexture(texture)
			super.setLightmapDisabled(false)
			GlStateManager.popMatrix()
		}
	}

	private fun renderTexture(texture: String) {
		GlStateManager.pushMatrix()

		GlStateManager.translate(-0.5f, 0f, 0.01f)
		GlStateManager.scale(TESR_MAGIC_NUMBER, -TESR_MAGIC_NUMBER, TESR_MAGIC_NUMBER)
		GlStateManager.glNormal3f(0.0f, 0.0f, 1.0f)
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

		RenderUtils.bindTexture(texture)
		drawScaledCustomSizeModalRect(0, 0, 0f, 0f, 16, 16, ONE_BLOCK_WIDTH.toInt(), ONE_BLOCK_WIDTH.toInt(), 16f, 16f, -1.2)

		GlStateManager.popMatrix()
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
	}
}
