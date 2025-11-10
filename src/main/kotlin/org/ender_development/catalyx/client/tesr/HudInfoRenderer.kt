package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.HudInfoLine
import org.ender_development.catalyx.tiles.helper.IHudInfoProvider
import org.ender_development.catalyx.utils.RenderUtils.FONT_RENDERER
import org.ender_development.catalyx.utils.RenderUtils.drawRectangle
import org.ender_development.catalyx.utils.extensions.getFacingFromEntity
import org.ender_development.catalyx.utils.extensions.glRotate

@SideOnly(Side.CLIENT)
object HudInfoRenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		if(tileEntity !is IHudInfoProvider)
			error("Tile ${tileEntity::class.java.canonicalName} doesn't implement IHudInfoProvider, but was passed into HudInfoRenderer#render()")

		if(!shouldRender(tileEntity))
			return

		var side = rendererDispatcher.cameraHitResult.sideHit
		if(side.axis === EnumFacing.Axis.Y)
			side = tileEntity.pos.getFacingFromEntity(rendererDispatcher.entity)
		val lines = tileEntity.getHudInfo(side)

		if(lines.isEmpty())
			return

		GlStateManager.pushMatrix()
		GlStateManager.translate(x + .5, y + 1, z + .5)
		side.glRotate()
		GlStateManager.translate(.0, .0, .5)

		super.setLightmapDisabled(true)
		renderText(lines)
		super.setLightmapDisabled(false)
		GlStateManager.popMatrix()
	}

	private fun shouldRender(te: BaseTile) =
		te.pos == rendererDispatcher.cameraHitResult.blockPos

	private fun renderText(messages: Array<HudInfoLine>) {
		GlStateManager.pushMatrix()

		GlStateManager.translate(-.5, .0, .01)
		GlStateManager.scale(TESR_MAGIC_NUMBER, -TESR_MAGIC_NUMBER, TESR_MAGIC_NUMBER)
		GlStateManager.glNormal3f(0f, 0f, 1f)
		GlStateManager.color(1f, 1f, 1f, 1f)

		val blockSize = .9 / TESR_MAGIC_NUMBER
		val padding = .05 / TESR_MAGIC_NUMBER

		val height = 11.0
		val logSize = messages.size
		var y = -height * logSize - height / 2
		for(message in messages) {
			if(message.background != null)
				drawRectangle(padding, y, blockSize, height, message.background, true, -.03)

			if(message.percent > 0 && message.percentColor != null) {
				val percent = message.percent.coerceIn(0f, 1f)
				drawRectangle(padding, y, blockSize * percent, height, message.percentColor!!, true, -.02)
			}

			if(message.border != null)
				drawRectangle(padding, y, blockSize, height, message.border, false, -.01)

			val maxWidth = blockSize.toInt() - 2
			val line = FONT_RENDERER.trimStringToWidth(message.text, maxWidth)
			val colour = message.color?.let { it.rgb and 0xFFFFFF } ?: 0xFFFFFF
			println("colour=$colour; GlSM state={r=${GlStateManager.colorState.red}; g=${GlStateManager.colorState.green}; b=${GlStateManager.colorState.blue}; a=${GlStateManager.colorState.alpha}}")
			if(message.alignment == HudInfoLine.TextAlign.LEFT)
				FONT_RENDERER.drawString(line, padding.toInt() + 1, y.toInt() + 2, colour)
			else {
				var x = FONT_RENDERER.getStringWidth(line).coerceAtMost(maxWidth)
				if(message.alignment == HudInfoLine.TextAlign.CENTER)
					x = x shr 1

				FONT_RENDERER.drawString(line, x, y.toInt() + 2, colour)
			}
			y += height
		}

		GlStateManager.popMatrix()
		GlStateManager.color(1f, 1f, 1f, 1f)
	}
}
