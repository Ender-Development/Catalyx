package org.ender_development.catalyx.client.tesr

import groovy.util.Eval.x
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.utils.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

abstract class AbstractTESRenderer : TileEntitySpecialRenderer<BaseTile>() {
	companion object {
		const val TESR_MAGIC_NUMBER = 0.0075
		const val ONE_BLOCK_WIDTH = 1.0 / TESR_MAGIC_NUMBER
	}

	abstract override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)

	/**
	 * Draws a scaled, textured, tiled modal rect. Adapted from the [net.minecraft.client.gui.Gui] class.
	 *
	 * @param u Texture U (or x) coordinate, in pixels
	 * @param v Texture V (or y) coordinate, in pixels
	 * @param uWidth Width of the rendered part of the texture, in pixels. Parts of the texture outside of it will wrap
	 * around
	 * @param vHeight Height of the rendered part of the texture, in pixels. Parts of the texture outside of it will
	 * wrap around
	 * @param tileWidth total width of the texture
	 * @param tileHeight total height of the texture
	 * @param zOffset Z offset to render at
	 */
	internal fun drawScaledCustomSizeModalRect(
		x: Int,
		y: Int,
		u: Float,
		v: Float,
		uWidth: Int,
		vHeight: Int,
		width: Int,
		height: Int,
		tileWidth: Float,
		tileHeight: Float,
		zOffset: Double = 0.0
	) {
		val f = 1.0F / tileWidth
		val f1 = 1.0F / tileHeight
		RenderUtils.BUFFER_BUILDER.begin(7, DefaultVertexFormats.POSITION_TEX);
		RenderUtils.BUFFER_BUILDER.pos(x.toDouble(), (y + height).toDouble(), zOffset).tex((u * f).toDouble(), ((v + vHeight) * f1).toDouble()).endVertex()
		RenderUtils.BUFFER_BUILDER.pos((x + width).toDouble(), (y + height).toDouble(), zOffset).tex(((u + uWidth) * f).toDouble(), ((v + vHeight) * f1).toDouble()).endVertex()
		RenderUtils.BUFFER_BUILDER.pos((x + width).toDouble(), y.toDouble(), zOffset).tex(((u + uWidth) * f).toDouble(), (v * f1).toDouble()).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x.toDouble(), y.toDouble(), zOffset).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
		RenderUtils.TESSELLATOR.draw()
	}

	internal fun drawRectangle(color: Color, filled: Boolean, x: Double, y: Double, width: Double, height: Double, zTranslate: Double) {
		val red = color.red / 255.0f
		val green = color.green / 255.0f
		val blue = color.blue / 255.0f
		val alpha = color.alpha / 255.0f

		GlStateManager.pushAttrib()
		GlStateManager.pushMatrix()

		if(!filled) {
			RenderUtils.BUFFER_BUILDER.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

			RenderUtils.BUFFER_BUILDER.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex()
		} else {
			RenderUtils.BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

			RenderUtils.BUFFER_BUILDER.pos(x, y + 0, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + 0, 0.0).color(red, green, blue, alpha).endVertex()
		}

		GlStateManager.translate(0.0, 0.0, zTranslate)
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
		GlStateManager.popAttrib()
	}
}
