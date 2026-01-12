package org.ender_development.catalyx.core.client.tesr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.blocks.helper.IOType
import org.ender_development.catalyx.core.tiles.BaseTile
import org.ender_development.catalyx.core.tiles.helper.IPortRenderer
import org.ender_development.catalyx.core.utils.RenderUtils
import org.ender_development.catalyx.core.utils.RenderUtils.drawScaledCustomSizeModalRect
import org.ender_development.catalyx.core.utils.extensions.glOffsetX
import org.ender_development.catalyx.core.utils.extensions.glOffsetZ
import org.ender_development.catalyx.core.utils.extensions.glRotate
import org.ender_development.catalyx.core.utils.extensions.glRotationAngle
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object IORenderer : AbstractTESRenderer() {
	override fun render(tileEntity: BaseTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
		if(tileEntity !is IPortRenderer)
			return

		EnumFacing.entries.forEach { side ->
			val state = tileEntity.getPortState(side)

			if(state === IOType.DEFAULT)
				return@forEach

			GlStateManager.pushMatrix()
			translateToSide(side, x, y, z)

			val texture = ResourceLocation(Reference.MODID, "textures/blocks/io/${state.name.lowercase()}.png")
			renderTexture(texture)
			GlStateManager.popMatrix()
		}
	}

	private fun renderTexture(texture: ResourceLocation) {
		GlStateManager.pushMatrix()

		GlStateManager.shadeModel(if(Minecraft.isAmbientOcclusionEnabled()) GL11.GL_SMOOTH else GL11.GL_FLAT)

		GlStateManager.translate(-.5, .0, .01)
		GlStateManager.scale(TESR_MAGIC_NUMBER, -TESR_MAGIC_NUMBER, TESR_MAGIC_NUMBER)
		GlStateManager.color(1f, 1f, 1f, 1f)

		RenderUtils.bindTexture(texture)
		drawScaledCustomSizeModalRect(.0, .0, .0, .0, 16.0, 16.0, ONE_BLOCK_WIDTH, ONE_BLOCK_WIDTH, 16.0, 16.0)

		GlStateManager.popMatrix()
	}

	/**
	 * Translates and rotates the GL matrix to render on the given side of a block at the given coordinates.
	 * Note: with this Implementation, the texture on side === DOWN is technically flipped top to bottom (i.e. ^ is v, …).
	 *
	 * This can be "fixed" by always doing .opposite in the horizontalFacing calculation, but makes the most sense this way,
	 * especially when it comes to respecting shading of the block textures.
	 *
	 * @param side The side to translate to
	 * @param x The x coordinate of the current rendering
	 * @param y The y coordinate of the current rendering
	 * @param z The z coordinate of the current rendering
	 * @see EnumFacing.glRotate
	 */
	private fun translateToSide(side: EnumFacing, x: Double, y: Double, z: Double) {
		if(side.axis === EnumFacing.Axis.Y) {
			// note: with this impl, the texture on side === DOWN is technically flipped top to bottom (i.e. ^ is v, …), this can be "fixed" by always doing .opposite here
			val horizontalFacing = Minecraft.getMinecraft().player.horizontalFacing.let {
				if(side === EnumFacing.UP)
					it.opposite
				else
					it
			}
			GlStateManager.translate(x + horizontalFacing.glOffsetX, y + .5, z + horizontalFacing.glOffsetZ)
			side.glRotate()
			GlStateManager.rotate(horizontalFacing.glRotationAngle - if(side == EnumFacing.DOWN && horizontalFacing.axis == EnumFacing.Axis.Z) 180f else 0f, 0f, 0f, 1f)
		} else {
			GlStateManager.translate(x + .5, y + 1, z + .5)
			side.glRotate()
		}
		GlStateManager.translate(.0, .0, .5)
	}
}
