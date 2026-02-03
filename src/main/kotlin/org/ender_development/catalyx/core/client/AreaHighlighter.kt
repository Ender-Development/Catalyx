package org.ender_development.catalyx.core.client

import io.netty.util.internal.ConcurrentSet
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.api.v1.client.interfaces.IAreaHighlighter
import org.ender_development.catalyx.core.utils.RenderUtils
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
internal class AreaHighlighter : IAreaHighlighter {
	override var shown = false
		private set
	override var drawOutlinesFor = emptyArray<Pair<Vec3d, Vec3d>>()
		private set
	override var r = 1f
		private set
	override var g = 1f
		private set
	override var b = 1f
		private set
	override var until = 0L
		private set
	override var thickness = 3f

	override fun highlightAreas(areas: Array<Pair<Vec3d, Vec3d>>, r: Float, g: Float, b: Float, time: Int) {
		if(areas.isEmpty())
			return

		drawOutlinesFor = areas
		shown = true
		this.r = r
		this.g = g
		this.b = b
		until = System.currentTimeMillis() + time
		show()
	}

	override fun hide() {
		eventHandlers.remove(::eventHandler)
		shown = false
		counter = 0
		counterDirection = 1
		drawOutlinesFor = emptyArray()
	}

	internal fun show() {
		eventHandlers.add(::eventHandler)
		shown = true
	}

	private var counter = 0
	private var counterDirection = 1
	
	private fun eventHandler(event: RenderWorldLastEvent) {
		if(!shown)
			return hide()

		val time = System.currentTimeMillis()

		if(time > until)
			return hide()

		// do this instead of just incrementing a float because precision makes it jittery (float going to like 1.0000001f, gl rolling over and interpreting it as 0f)
		counter += counterDirection
		if(counter == 0 || counter == 50)
			counterDirection *= -1

		val alpha = .5f + counter / 100f

		val p = Minecraft.getMinecraft().player
		val translateX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks
		val translateY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks
		val translateZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks

		GlStateManager.pushMatrix()
		GlStateManager.enableBlend()
		GlStateManager.color(r, g, b, alpha)
		GlStateManager.glLineWidth(thickness)
		GlStateManager.translate(-translateX, -translateY, -translateZ)

		GlStateManager.disableDepth()
		GlStateManager.disableTexture2D()
		
		RenderUtils.bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
		
		drawOutlinesFor.forEach { (from, to) ->
			renderOutline(RenderUtils.bufferBuilder, from.x, from.y, from.z, to.x, to.y, to.z, r, g, b, alpha)
		}

		RenderUtils.tessellator.draw()

		GlStateManager.enableTexture2D()
		GlStateManager.enableDepth()
		GlStateManager.disableBlend()
		GlStateManager.popMatrix()
	}

	private fun renderOutline(buffer: BufferBuilder, mx: Double, my: Double, mz: Double, tx: Double, ty: Double, tz: Double, red: Float, green: Float, blue: Float, alpha: Float) {
		buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, ty, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, ty, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, mz).color(red, green, blue, alpha).endVertex()

		buffer.pos(mx, ty, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, ty, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, ty, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, mz).color(red, green, blue, alpha).endVertex()

		buffer.pos(tx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, my, mz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, ty, mz).color(red, green, blue, alpha).endVertex()

		buffer.pos(mx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(tx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, my, tz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx, ty, tz).color(red, green, blue, alpha).endVertex()
	}

	internal companion object {
		val eventHandlers = ConcurrentSet<(RenderWorldLastEvent) -> Unit>()
	}
}
