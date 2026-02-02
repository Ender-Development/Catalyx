package org.ender_development.catalyx.core.client

import io.netty.util.internal.ConcurrentSet
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.api.v1.client.interfaces.IAreaHighlighter
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
internal class AreaHighlighter : IAreaHighlighter {
	private var counter = 0
	private var counterDirection = 1

	override var shown = false
		private set
	override var x1 = .0
		private set
	override var y1 = .0
		private set
	override var z1 = .0
		private set
	override val pos1
		get() = BlockPos(x1, y1, z1)
	override var x2 = .0
		private set
	override var y2 = .0
		private set
	override var z2 = .0
		private set
	override val pos2
		get() = BlockPos(x2, y2, z2)
	override var drawBlockPositions = false
		private set
	override var drawnBlockPositions = emptyArray<BlockPos>()
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

	override fun highlightArea(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, r: Float, g: Float, b: Float, time: Int) {
		this.x1 = x1
		this.y1 = y1
		this.z1 = z1
		this.x2 = x2
		this.y2 = y2
		this.z2 = z2
		this.r = r
		this.g = g
		this.b = b
		until = System.currentTimeMillis() + time.toLong()
		show()
	}

	override fun highlightBlocks(blockPositions: Array<BlockPos>, r: Float, g: Float, b: Float, time: Int) {
		drawBlockPositions = true
		drawnBlockPositions = blockPositions
		this.r = r
		this.g = g
		this.b = b
		until = System.currentTimeMillis() + time.toLong()
		show()
	}

	override fun hide() {
		eventHandlers.remove(::eventHandler)
		shown = false
		counter = 0
		counterDirection = 1
		if(drawBlockPositions) {
			drawBlockPositions = false
			drawnBlockPositions = emptyArray()
		}
	}

	internal fun show() {
		eventHandlers.add(::eventHandler)
		shown = true
	}

	internal companion object {
		val eventHandlers = ConcurrentSet<(RenderWorldLastEvent) -> Unit>()
	}

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
		val doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks
		val doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks
		val doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks

		GlStateManager.pushMatrix()
		GlStateManager.enableBlend()
		GlStateManager.color(r, g, b, alpha)
		GlStateManager.glLineWidth(thickness)
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ)

		GlStateManager.disableDepth()
		GlStateManager.disableTexture2D()

		val tessellator = Tessellator.getInstance()
		val buffer = tessellator.buffer
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
		if(!drawBlockPositions)
			renderOutline(buffer, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, r, g, b, alpha)
		else
			drawnBlockPositions.forEach {
				renderOutline(buffer, it.x.toDouble(), it.y.toDouble(), it.z.toDouble(), 1.0, 1.0, 1.0, r, g, b, alpha)
			}

		tessellator.draw()

		GlStateManager.enableTexture2D()
		GlStateManager.enableDepth()
		GlStateManager.disableBlend()
		GlStateManager.popMatrix()
	}

	private fun renderOutline(buffer: BufferBuilder, mx: Double, my: Double, mz: Double, dx: Double, dy: Double, dz: Double, red: Float, green: Float, blue: Float, alpha: Float) {
		buffer.pos(mx,      my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my + dy, mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz     ).color(red, green, blue, alpha).endVertex()

		buffer.pos(mx,      my + dy, mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my + dy, mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz     ).color(red, green, blue, alpha).endVertex()

		buffer.pos(mx + dx, my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my,      mz     ).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my + dy, mz     ).color(red, green, blue, alpha).endVertex()

		buffer.pos(mx,      my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx + dx, my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my,      mz + dz).color(red, green, blue, alpha).endVertex()
		buffer.pos(mx,      my + dy, mz + dz).color(red, green, blue, alpha).endVertex()
	}
}
