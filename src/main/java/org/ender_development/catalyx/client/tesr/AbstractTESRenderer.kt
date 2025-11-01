package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.utils.RenderUtils
import org.ender_development.catalyx.utils.extensions.destructFloat
import org.lwjgl.opengl.GL11
import java.awt.Color

abstract class AbstractTESRenderer : TileEntitySpecialRenderer<BaseTile>() {
	companion object {
		const val TESR_MAGIC_NUMBER = 0.0075
		const val ONE_BLOCK_WIDTH = 1 / TESR_MAGIC_NUMBER
	}

	// Override super method to force use of BaseTile
	abstract override fun render(te: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)

	/**
	 * Draws a scaled, textured, tiled modal rect. Adapted from the [net.minecraft.client.gui.Gui] class.
	 *
	 * @param u Texture U (or x) coordinate, in pixels
	 * @param v Texture V (or y) coordinate, in pixels
	 * @param uWidth Width of the rendered part of the texture, in pixels. Texture will be wrapped.
	 * @param vHeight Height of the rendered part of the texture, in pixels. Texture will be wrapped.
	 * @param tileWidth total width of the texture
	 * @param tileHeight total height of the texture
	 * @param zOffset Z offset to render at
	 * @param color Color to tint the rendered texture with
	 */
	fun drawScaledCustomSizeModalRect(
		x: Double,
		y: Double,
		u: Double,
		v: Double,
		uWidth: Double,
		vHeight: Double,
		width: Double,
		height: Double,
		tileWidth: Double,
		tileHeight: Double,
		zOffset: Double = .0,
		color: Color = Color.WHITE
	) {
		val tw = 1 / tileWidth
		val th = 1 / tileHeight
		val (red, green, blue, alpha) = color.destructFloat()
		RenderUtils.BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		RenderUtils.BUFFER_BUILDER.pos(x, y + height, zOffset).tex(u * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, zOffset).tex((u + uWidth) * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y, zOffset).tex((u + uWidth) * tw, v * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x, y, zOffset).tex(u * tw, v * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.TESSELLATOR.draw()
	}

	fun drawRectangle(color: Color, filled: Boolean, x: Double, y: Double, width: Double, height: Double, zTranslate: Double) {
		val red = color.red / 255f
		val green = color.green / 255f
		val blue = color.blue / 255f
		val alpha = color.alpha / 255f

		GlStateManager.pushMatrix()

		if(!filled) {
			RenderUtils.BUFFER_BUILDER.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

			RenderUtils.BUFFER_BUILDER.pos(x, y, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y, .0).color(red, green, blue, alpha).endVertex()
		} else {
			RenderUtils.BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

			RenderUtils.BUFFER_BUILDER.pos(x, y + 0, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + 0, .0).color(red, green, blue, alpha).endVertex()
		}

		GlStateManager.translate(.0, .0, zTranslate)
		GlStateManager.enableBlend()
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
		GlStateManager.disableLighting()
		GlStateManager.disableTexture2D()
		GlStateManager.depthMask(false)
		RenderUtils.TESSELLATOR.draw()
		GlStateManager.depthMask(true)
		GlStateManager.enableTexture2D()
		GlStateManager.enableLighting()
		GlStateManager.disableBlend()
		GlStateManager.popMatrix()
	}
}
