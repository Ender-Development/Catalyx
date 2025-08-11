package io.enderdev.catalyx.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager

class RedstoneButtonWrapper(x: Int, y: Int) : AbstractButtonWrapper(x, y) {
	enum class State {
		ON, OFF
	}

	var needsPower = State.OFF

	override val drawButton: () -> GuiButton.(Minecraft, Int, Int, Float) -> Unit = { { mc, mouseX, mouseY, partialTicks ->
		mc.textureManager.bindTexture(textureLocation)
		GlStateManager.color(1f, 1f, 1f)
		drawTexturedModalRect(this.x, this.y, 64, if(needsPower == State.ON) 16 else 0, 16, 16)
	} }

	companion object {
		init {
			registerWrapper(this::class.java)
		}
	}
}
