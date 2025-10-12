package org.ender_development.catalyx.utils

import net.minecraft.client.Minecraft
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
import org.lwjgl.opengl.GL11

object RenderUtils {
	val BLOCK_TEX: ResourceLocation = TextureMap.LOCATION_BLOCKS_TEXTURE

	val minecraft: Minecraft = Minecraft.getMinecraft()
	val renderEngine: TextureManager = minecraft.renderEngine

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
}
