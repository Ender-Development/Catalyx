package org.ender_development.catalyx.client.tesr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.blocks.helper.IOType
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.IPortRenderer
import org.ender_development.catalyx.utils.RenderUtils
import org.ender_development.catalyx.utils.extensions.glOffsetX
import org.ender_development.catalyx.utils.extensions.glOffsetZ
import org.ender_development.catalyx.utils.extensions.glRotate
import org.ender_development.catalyx.utils.extensions.glRotationAngle
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

			// TODO remove this pushAttrib/popAttrib, the JavaDoc for it even says not to use it
			// push/pop attrib can mess with GlSM state
			GlStateManager.pushAttrib()
			GlStateManager.pushMatrix()

			translateToSide(side, x, y, z)

			val texture = ResourceLocation(Reference.MODID, "textures/blocks/io/${state.name.lowercase()}.png")
			renderTexture(texture)

			GlStateManager.popMatrix()
			GlStateManager.popAttrib()
		}
	}

	private fun renderTexture(texture: ResourceLocation) {
		GlStateManager.pushMatrix()

		GlStateManager.shadeModel(if(Minecraft.isAmbientOcclusionEnabled()) GL11.GL_SMOOTH else GL11.GL_FLAT)

		GlStateManager.translate(-.5, .0, .01)
		GlStateManager.scale(TESR_MAGIC_NUMBER, -TESR_MAGIC_NUMBER, TESR_MAGIC_NUMBER)
		GlStateManager.color(1f, 1f, 1f, 1f)

		RenderUtils.bindTexture(texture)
		drawScaledCustomSizeModalRectLegacy(.0, .0, .0, .0, 16.0, 16.0, ONE_BLOCK_WIDTH, ONE_BLOCK_WIDTH, 16.0, 16.0)

		GlStateManager.popMatrix()
	}

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
