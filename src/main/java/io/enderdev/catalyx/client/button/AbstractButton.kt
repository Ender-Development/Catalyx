package io.enderdev.catalyx.client.button

import io.enderdev.catalyx.Reference
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

abstract class AbstractButton(x: Int, y: Int) : GuiButton(CatalyxButtons.nextId(), x, y, 16, 16, "") {
	open val textureLocation = ResourceLocation(Reference.MODID, "textures/gui/container/gui.png")

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(!visible)
			return

		hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		if(!hovered)
			return

		mc.textureManager.bindTexture(textureLocation)
		GlStateManager.color(1f, 1f, 1f)
		drawTexturedModalRect(x, y, 48, 48, width, height)
	}

	open fun writeExtraData(buf: ByteBuf) {}
	open fun readExtraData(buf: ByteBuf) {}
}
