@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import org.ender_development.catalyx.api.v1.common.extensions.destructFloat
import org.ender_development.catalyx.api.v1.common.extensions.getColor
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtils {
	val minecraft: Minecraft = Minecraft.getMinecraft()

	val tessellator: Tessellator = Tessellator.getInstance()
	val bufferBuilder: BufferBuilder = tessellator.buffer
	val fontRenderer: FontRenderer = minecraft.fontRenderer
	val textureManager: TextureManager = minecraft.renderEngine

	val blockTexture: ResourceLocation = TextureMap.LOCATION_BLOCKS_TEXTURE

	inline fun bindBlockTexture() =
		bindTexture(blockTexture)

	inline fun bindTexture(tex: ResourceLocation) =
		textureManager.bindTexture(tex)

	fun getStillTexture(fluid: FluidStack?) =
		fluid?.fluid?.let(::getStillTexture)

	fun getStillTexture(fluid: Fluid) =
		fluid.still?.toString()?.let(minecraft.textureMapBlocks::getTextureExtry)

	inline fun renderGuiTank(tank: FluidTank, x: Double, y: Double, width: Double, height: Double) =
		renderGuiTank(tank.fluid, tank.capacity, x, y, width, height)

	fun renderGuiTank(fluid: FluidStack?, capacity: Int, x: Double, y: Double, width: Double, height: Double) {
		if(fluid == null || fluid.amount <= 0)
			return

		val sprite = getStillTexture(fluid) ?: return

		val bottomY = y + height
		val topY = bottomY - height * fluid.amount.coerceAtMost(capacity) / capacity

		val (red, green, blue) = Color(fluid.getColor()).destructFloat()
		GlStateManager.color(red, green, blue, 1f)
		GlStateManager.enableBlend()

		bindBlockTexture()

		// in any normal programming language, this would just be something like `for(double drawY = topY; drawY < bottomY; drawY += 16)`
		var drawY = topY
		while(drawY < bottomY) {
			var xOffset = 0
			while(xOffset < width) {
				val drawWidth = (width - xOffset).coerceAtMost(16.0)
				val drawHeight = (bottomY - drawY).coerceAtMost(16.0)

				drawTexturedModalRect(x + xOffset, drawY, sprite, drawWidth, drawHeight)
				xOffset += 16
			}
			drawY += 16
		}
		GlStateManager.disableBlend()
	}

	fun renderText(text: String, x: Double, y: Double, color: Int, scale: Double = 1.0, shadow: Boolean = false) {
		GlStateManager.disableCull()
		GlStateManager.enableTexture2D()
		GlStateManager.disableLighting()
		GlStateManager.enableBlend()
		GlStateManager.disableAlpha()
		GlStateManager.disableDepth()
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)

		GlStateManager.pushMatrix()
		GlStateManager.translate(x, y, .0)
		GlStateManager.scale(scale, scale, .0)
		fontRenderer.drawString(text, 0f, 0f, color, shadow)
		GlStateManager.popMatrix()
	}

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
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
		bufferBuilder.pos(x, y + height, zOffset).tex(u * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		bufferBuilder.pos(x + width, y + height, zOffset).tex((u + uWidth) * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		bufferBuilder.pos(x + width, y, zOffset).tex((u + uWidth) * tw, v * th).color(red, green, blue, alpha).endVertex()
		bufferBuilder.pos(x, y, zOffset).tex(u * tw, v * th).color(red, green, blue, alpha).endVertex()
		tessellator.draw()
	}

	/**
	 * Draw a colored 2D rectangle.
	 *
	 * @param filled Whether to draw a filled rectangle, or just its outline.
	 */
	fun drawRectangle(x: Double, y: Double, width: Double, height: Double, color: Color, filled: Boolean, zTranslate: Double = .0) {
		val (red, green, blue, alpha) = color.destructFloat()

		GlStateManager.pushMatrix()

		if(!filled) {
			bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

			bufferBuilder.pos(x, y, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x, y, .0).color(red, green, blue, alpha).endVertex()
		} else {
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

			bufferBuilder.pos(x, y + 0, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			bufferBuilder.pos(x + width, y + 0, .0).color(red, green, blue, alpha).endVertex()
		}

		GlStateManager.translate(.0, .0, zTranslate)
		GlStateManager.enableBlend()
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
		GlStateManager.disableLighting()
		GlStateManager.disableTexture2D()
		GlStateManager.depthMask(false)
		tessellator.draw()
		GlStateManager.depthMask(true)
		GlStateManager.enableTexture2D()
		GlStateManager.enableLighting()
		GlStateManager.disableBlend()
		GlStateManager.popMatrix()
	}
	
	const val MAGIC_NUMBER = 1.0 / 256.0

	/**
	 * Draw a 2D textured rectangle. Adapted from [Gui#drawTexturedModalRect][net.minecraft.client.gui.Gui.drawTexturedModalRect].
	 */
	fun drawTexturedModalRect(x: Double, y: Double, u: Float, v: Float, width: Double, height: Double, zLevel: Double = .0) {
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
		bufferBuilder.pos(x, y + height, zLevel).tex(u * MAGIC_NUMBER, (v + height) * MAGIC_NUMBER).endVertex()
		bufferBuilder.pos(x + width, y + height, zLevel).tex((u + width) * MAGIC_NUMBER, (v + height) * MAGIC_NUMBER).endVertex()
		bufferBuilder.pos(x + width, y, zLevel).tex((u + width) * MAGIC_NUMBER, v * MAGIC_NUMBER).endVertex()
		bufferBuilder.pos(x, y, zLevel).tex(u * MAGIC_NUMBER, v * MAGIC_NUMBER).endVertex()
		tessellator.draw()
	}

	/**
	 * Draw a textured rectangle using the [sprite]. Adapted from [Gui#drawTexturedModalRect][net.minecraft.client.gui.Gui.drawTexturedModalRect]
	 *
	 * @param sprite Sprite to render
	 * @param zLevel Z level to render at
	 */
	fun drawTexturedModalRect(x: Double, y: Double, sprite: TextureAtlasSprite, width: Double, height: Double, zLevel: Double = .0) {
		val minU = sprite.minU.toDouble()
		val maxU = sprite.maxU.toDouble()
		val minV = sprite.minV.toDouble()
		val maxV = sprite.maxV.toDouble()
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
		bufferBuilder.pos(x, y + height, zLevel).tex(minU, maxV).endVertex()
		bufferBuilder.pos(x + width, y + height, zLevel).tex(maxU, maxV).endVertex()
		bufferBuilder.pos(x + width, y, zLevel).tex(maxU, minV).endVertex()
		bufferBuilder.pos(x, y, zLevel).tex(minU, minV).endVertex()
		tessellator.draw()
	}
}
