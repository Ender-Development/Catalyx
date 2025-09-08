package org.ender_development.catalyx.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager

class PauseButtonWrapper(x: Int, y: Int) : AbstractButtonWrapper(x, y) {
	enum class State {
		PAUSED, RUNNING
	}

	var isPaused = State.RUNNING

	override val drawButton: () -> GuiButton.(Minecraft, Int, Int, Float) -> Unit = { { mc, mouseX, mouseY, partialTicks ->
		mc.textureManager.bindTexture(textureLocation)
		GlStateManager.color(1f, 1f, 1f)
		drawTexturedModalRect(this.x, this.y, 48, if(isPaused == State.PAUSED) 16 else 0, 16, 16)
	} }
}
