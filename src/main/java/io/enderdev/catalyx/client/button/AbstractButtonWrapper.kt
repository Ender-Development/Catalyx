package io.enderdev.catalyx.client.button

import io.enderdev.catalyx.Reference
import io.enderdev.catalyx.utils.SideUtils
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.jvm.java

abstract class AbstractButtonWrapper(x: Int, y: Int, width: Int = 16, height: Int = 16) {
	open val textureLocation = ResourceLocation(Reference.MODID, "textures/gui/container/gui.png")
	open val drawDefaultHoverOverlay = true

	@SideOnly(Side.CLIENT)
	class WrappedGuiButton(x: Int, y: Int, width: Int, height: Int, val wrapper: AbstractButtonWrapper) : GuiButton(-142, x, y, width, height, "") {
		override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
			if(!visible)
				return

			wrapper.drawButton()(this, mc, mouseX, mouseY, partialTicks)

			if(!wrapper.drawDefaultHoverOverlay)
				return

			hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
			if(!hovered)
				return

			mc.textureManager.bindTexture(wrapper.textureLocation)
			GlStateManager.color(1f, 1f, 1f)
			// +1/-1 to account for the border and only highlight the contents
			drawRect(x + 1, y + 1, x + width - 1, y + height - 1, 0x64ffffff)
		}
	}

	/** If you change this variable, the underlying button is also updated */
	var x: Int = x
		set(value) {
			field = value
			button?.x = value
		}

	/** If you change this variable, the underlying button is also updated */
	var y: Int = y
		set(value) {
			field = value
			button?.y = value
		}

	/** If you change this variable, the underlying button is also updated */
	var width: Int = width
		set(value) {
			field = value
			button?.width = value
		}

	/** If you change this variable, the underlying button is also updated */
	var height: Int = height
		set(value) {
			field = value
			button?.height = value
		}

	/**
	 * Guaranteed to be non-null on client-side
	 */
	open val button = if(SideUtils.isClient)
		WrappedGuiButton(x, y, width, height, this)
	else
		null

	open fun readExtraData(buf: ByteBuf) {}
	open fun writeExtraData(buf: ByteBuf) {}

	abstract val drawButton: () -> GuiButton.(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) -> Unit

	init {
		buttonWrappers.add(this::class.java.name)
	}

	companion object {
		internal val buttonWrappers = hashSetOf<String>()
		inline fun <reified T : AbstractButtonWrapper> getWrapper(button: GuiButton): T? {
			if(button !is WrappedGuiButton)
				return null

			return button.wrapper as? T
		}

		/** Explicitly register wrapper, use if you cannot guarantee there's gonna be an instance created on server-side */
		fun registerWrapper(wrapper: Class<*>) {
			buttonWrappers.add(wrapper.name.removeSuffix("\$Companion"))
		}
	}
}
