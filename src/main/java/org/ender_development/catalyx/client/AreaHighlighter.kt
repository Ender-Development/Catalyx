package org.ender_development.catalyx.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * A helper class allowing you to highlight an area or a block in 3D space
 * @see [highlightBlock]
 * @see [highlightBlocks]
 * @see [highlightArea]
 */
@SideOnly(Side.CLIENT)
class AreaHighlighter {
	private var counter = 0
	private var counterDirection = 1

	var shown = false
		private set
	var x1 = .0
		private set
	var y1 = .0
		private set
	var z1 = .0
		private set
	val pos1
		get() = BlockPos(x1, y1, z1)
	var x2 = .0
		private set
	var y2 = .0
		private set
	var z2 = .0
		private set
	val pos2
		get() = BlockPos(x2, y2, z2)
	var drawBlockPositions = false
		private set
	var drawnBlockPositions = emptyArray<BlockPos>()
		private set
	var r = 1f
		private set
	var g = 1f
		private set
	var b = 1f
		private set
	var until = 0L
		private set
	var thickness = 3f

	/**
	 * r, g, b are colours between 0 and 1, time is in milliseconds
	 */
	fun highlightBlock(pos: BlockPos, r: Float, g: Float, b: Float, time: Int) {
		x1 = pos.x.toDouble()
		y1 = pos.y.toDouble()
		z1 = pos.z.toDouble()
		x2 = x1 + 1
		y2 = y1 + 1
		z2 = z1 + 1
		this.r = r
		this.g = g
		this.b = b
		until = System.currentTimeMillis() + time.toLong()
		show()
	}

	/**
	 * r, g, b are colours between 0 and 1, time is in milliseconds
	 */
	fun highlightArea(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, r: Float, g: Float, b: Float, time: Int) {
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

	/**
	 * r, g, b are colours between 0 and 1, time is in milliseconds
	 */
	fun highlightBlocks(blockPositions: Array<BlockPos>, r: Float, g: Float, b: Float, time: Int) {
		drawBlockPositions = true
		drawnBlockPositions = blockPositions
		this.r = r
		this.g = g
		this.b = b
		until = System.currentTimeMillis() + time.toLong()
		show()
	}

	/**
	 * call this if you want to prematurely hide the block highlight;
	 * otherwise, this is automatically called after the {time} passes
	 */
	fun hide() {
		eventHandlers.remove(::eventHandler)
		shown = false
		counter = 0
		counterDirection = 1
		drawBlockPositions = false
		drawnBlockPositions = emptyArray()
	}

	internal fun show() {
		eventHandlers.add(::eventHandler)
		shown = true
	}

	internal companion object {
		val eventHandlers = hashSetOf<(RenderWorldLastEvent) -> Unit>()
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
