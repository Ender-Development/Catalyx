package org.ender_development.catalyx.client.tesr

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.utils.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

abstract class AbstractTESRenderer : TileEntitySpecialRenderer<BaseTile>() {
	companion object {
		const val TESR_MAGIC_NUMBER = 0.0075
		const val ONE_BLOCK_WIDTH = 1 / TESR_MAGIC_NUMBER
	}

	abstract override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)

	internal fun drawScaledCustomSizeModalRectLegacy(x: Double, y: Double, u: Double, v: Double, uWidth: Double, vHeight: Double, width: Double, height: Double, tileWidth: Double, tileHeight: Double, zOffset: Double = .0) {
		val tw = 1 / tileWidth
		val th = 1 / tileHeight
		RenderUtils.BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		RenderUtils.BUFFER_BUILDER.pos(x, y + height, zOffset).tex(u * tw, (v + vHeight) * th).color(1f,1f,1f,1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, zOffset).tex((u + uWidth) * tw, (v + vHeight) * th).color(1f,1f,1f,1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y, zOffset).tex((u + uWidth) * tw, v * th).color(1f,1f,1f,1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x, y, zOffset).tex(u * tw, v * th).color(1f,1f,1f,1f).endVertex()
		RenderUtils.TESSELLATOR.draw()
	}
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
		light: Pair<Int, Int>
	) {
		val tw = 1 / tileWidth
		val th = 1 / tileHeight
		val s = light.first
		val b = light.second
		val brightness = (max(s, b).toFloat()).coerceAtLeast(0.01f)
		RenderUtils.BUFFER_BUILDER.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		RenderUtils.BUFFER_BUILDER.pos(x, y + height, zOffset).tex(u * tw, (v + vHeight) * th).lightmap(s, b).color(brightness, brightness, brightness, 1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, zOffset).tex((u + uWidth) * tw, (v + vHeight) * th).lightmap(s, b).color(brightness, brightness, brightness, 1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y, zOffset).tex((u + uWidth) * tw, v * th).lightmap(s, b).color(brightness, brightness, brightness, 1f).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x, y, zOffset).tex(u * tw, v * th).lightmap(s, b).color(brightness, brightness, brightness, 1f).endVertex()
		RenderUtils.TESSELLATOR.draw()
	}

	internal fun drawRectangle(color: Color, filled: Boolean, x: Double, y: Double, width: Double, height: Double, zTranslate: Double) {
		val red = color.red / 255f
		val green = color.green / 255f
		val blue = color.blue / 255f
		val alpha = color.alpha / 255f

		GlStateManager.pushAttrib() // push/pop attrib can mess with GlSM state, is this really needed?
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
		GlStateManager.popAttrib()
	}
}
