package org.ender_development.catalyx.client.tesr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.HudInfoLine
import org.ender_development.catalyx.tiles.helper.IHudInfoProvider
import org.ender_development.catalyx.utils.extensions.getFacingFromEntity
import kotlin.math.roundToInt

@SideOnly(Side.CLIENT)
object HudInfoRenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		val provider = (tileEntity as? IHudInfoProvider) ?: return
		if(!shouldRender(tileEntity))
			return

		var side = rendererDispatcher.cameraHitResult.sideHit
		if(side == EnumFacing.DOWN || side == EnumFacing.UP)
			side = tileEntity.pos.getFacingFromEntity(rendererDispatcher.entity)
		val lines = provider.getHudInfo(side)

		if(lines.isEmpty())
			return

		GlStateManager.pushMatrix()
		GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
		when(side) {
			EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
			EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
			EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
			else -> {}
		}
		GlStateManager.translate(0.0, 0.0, 0.5)

		super.setLightmapDisabled(true)
		renderText(lines)
		super.setLightmapDisabled(false)
		GlStateManager.popMatrix()
	}

	private fun shouldRender(te: BaseTile) =
		(rendererDispatcher.cameraHitResult != null) && (te.pos == rendererDispatcher.cameraHitResult.blockPos)

	private fun renderText(messages: List<HudInfoLine>) {
		val font = Minecraft.getMinecraft().fontRenderer
		GlStateManager.pushMatrix()

		GlStateManager.translate(-0.5f, 0f, 0.01f)
		GlStateManager.scale(TESR_MAGIC_NUMBER, -TESR_MAGIC_NUMBER, TESR_MAGIC_NUMBER)
		GlStateManager.glNormal3f(0.0f, 0.0f, 1.0f)
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

		val blockSize = (.9f / TESR_MAGIC_NUMBER).roundToInt()
		val padding = (.05f / TESR_MAGIC_NUMBER).roundToInt()

		val height = 11
		val logSize = messages.size
		var y = -height * logSize - height / 2
		for(ctl in messages) {
			if(ctl.background != null)
				drawRectangle(ctl.background, true, padding.toDouble(), y.toDouble(), blockSize.toDouble(), height.toDouble(), -0.03)

			if((ctl.percent > 0) && (ctl.percentColor != null)) {
				val percent = ctl.percent.toDouble().coerceIn(0.0, 1.0)
				drawRectangle(ctl.percentColor!!, true, padding.toDouble(), y.toDouble(), blockSize * percent, height.toDouble(), -0.02)
			}

			if(ctl.border != null)
				drawRectangle(ctl.border, false, padding.toDouble(), y.toDouble(), blockSize.toDouble(), height.toDouble(), -0.01)

			val line = font.trimStringToWidth(ctl.text, blockSize - 2)
			when(ctl.alignment) {
				HudInfoLine.TextAlign.LEFT -> font.drawString(
					line, padding + 1, y + 2, if(ctl.color == null) 16777215 else ctl.color.rgb
				)
				HudInfoLine.TextAlign.RIGHT -> font.drawString(
					line, padding + 1 + blockSize - 2 - font.getStringWidth(ctl.text).coerceAtMost(blockSize - 2), y + 2, if(ctl.color == null) 16777215 else ctl.color.rgb
				)
				HudInfoLine.TextAlign.CENTER -> font.drawString(
					line, padding + 1 + (blockSize - 2 - font.getStringWidth(ctl.text).coerceAtMost(blockSize - 2)) / 2, y + 2, if(ctl.color == null) 16777215 else ctl.color.rgb
				)
			}
			y += height
		}

		GlStateManager.popMatrix()
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
	}
}
