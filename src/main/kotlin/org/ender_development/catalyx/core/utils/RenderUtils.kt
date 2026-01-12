package org.ender_development.catalyx.core.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import org.ender_development.catalyx.core.utils.extensions.destructFloat
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtils {
	val minecraft: Minecraft = Minecraft.getMinecraft()

	val TESSELLATOR: Tessellator = Tessellator.getInstance()
	val BUFFER_BUILDER: BufferBuilder = TESSELLATOR.buffer
	val FONT_RENDERER: FontRenderer = minecraft.fontRenderer
	val renderEngine: TextureManager = minecraft.renderEngine

	val BLOCK_TEX: ResourceLocation = TextureMap.LOCATION_BLOCKS_TEXTURE

	fun bindBlockTexture() =
		renderEngine.bindTexture(BLOCK_TEX)

	fun bindTexture(string: String) =
		renderEngine.bindTexture(ResourceLocation(string))

	fun bindTexture(tex: ResourceLocation) =
		renderEngine.bindTexture(tex)

	fun getStillTexture(fluid: FluidStack?) =
		fluid?.fluid?.let {
			getStillTexture(it)
		}

	fun getStillTexture(fluid: Fluid) =
		fluid.still?.let {
			minecraft.textureMapBlocks.getTextureExtry("$it")
		}

	fun renderGuiTank(tank: FluidTank, x: Double, y: Double, zLevel: Double, width: Double, height: Double) =
		renderGuiTank(tank.fluid, tank.capacity, tank.fluidAmount, x, y, zLevel, width, height)

	fun renderGuiTank(fluid: FluidStack?, capacity: Int, amount: Int, x: Double, y: Double, zLevel: Double, width: Double, height: Double) {
		if(fluid == null || fluid.fluid == null || fluid.amount <= 0)
			return

		val icon = getStillTexture(fluid) ?: return

		val renderAmount = (amount * height / capacity).coerceIn(.0, height)
		val posY = (y + height - renderAmount)

		bindBlockTexture()
		val color = fluid.fluid.getColor(fluid)
		GL11.glColor3ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte())

		// TODO clean up this mess
		GlStateManager.enableBlend()
		var i = 0
		while(i < width) {
			var j = 0
			while(j < renderAmount) {
				val drawWidth = (width - i).coerceAtMost(16.0)
				val drawHeight = (renderAmount - j).coerceAtMost(16.0)

				val drawX = x + i
				val drawY = posY + j

				val minU = icon.minU.toDouble()
				val maxU = icon.maxU.toDouble()
				val minV = icon.minV.toDouble()
				val maxV = icon.maxV.toDouble()

				val tessellator = Tessellator.getInstance()
				val tes = tessellator.buffer
				tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
				tes.pos(drawX, drawY + drawHeight, 0.0).tex(minU, minV + (maxV - minV) * drawHeight / 16f).endVertex()
				tes.pos(drawX + drawWidth, drawY + drawHeight, 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV + (maxV - minV) * drawHeight / 16f).endVertex()
				tes.pos(drawX + drawWidth, drawY, 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV).endVertex()
				tes.pos(drawX, drawY, 0.0).tex(minU, minV).endVertex()
				tessellator.draw()
				j += 16
			}
			i += 16
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
		FONT_RENDERER.drawString(text, 0f, 0f, color, shadow)
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
		RenderUtils.BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		RenderUtils.BUFFER_BUILDER.pos(x, y + height, zOffset).tex(u * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, zOffset).tex((u + uWidth) * tw, (v + vHeight) * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x + width, y, zOffset).tex((u + uWidth) * tw, v * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.BUFFER_BUILDER.pos(x, y, zOffset).tex(u * tw, v * th).color(red, green, blue, alpha).endVertex()
		RenderUtils.TESSELLATOR.draw()
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
			RenderUtils.BUFFER_BUILDER.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

			RenderUtils.BUFFER_BUILDER.pos(x, y, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y + height, .0).color(red, green, blue, alpha).endVertex()
			RenderUtils.BUFFER_BUILDER.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
			BUFFER_BUILDER.pos(x + width, y, .0).color(red, green, blue, alpha).endVertex()
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
	
	const val MAGIC_NUMBER = 0.00390625

	/**
	 * Draw a 2D textured rectangle. Adapted from [Gui#drawTexturedModalRect][net.minecraft.client.gui.Gui.drawTexturedModalRect].
	 */
	fun drawTexturedModalRect(x: Double, y: Double, u: Float, v: Float, width: Double, height: Double, zLevel: Double = .0) {
		BUFFER_BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
		BUFFER_BUILDER.pos(x, y + height, zLevel).tex(u * MAGIC_NUMBER, (v + height) * MAGIC_NUMBER).endVertex()
		BUFFER_BUILDER.pos(x + width, y + height, zLevel).tex((u + width) * MAGIC_NUMBER, (v + height) * MAGIC_NUMBER).endVertex()
		BUFFER_BUILDER.pos(x + width, y, zLevel).tex((u + width) * MAGIC_NUMBER, v * MAGIC_NUMBER).endVertex()
		BUFFER_BUILDER.pos(x, y, zLevel).tex(u * MAGIC_NUMBER, v * MAGIC_NUMBER).endVertex()
		TESSELLATOR.draw()
	}
}
