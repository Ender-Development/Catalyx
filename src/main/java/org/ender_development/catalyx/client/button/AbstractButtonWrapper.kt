package org.ender_development.catalyx.client.button

import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.SideUtils
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Wrapper class for stateful buttons sent from client-side to server-side
 * When creating a wrapper
 * - if your button/wrapper has any state that will need to be sent to the server-side, read it in readExtraData and write it in writeExtraData (x, y, width, height are automatically sent)
 * - make sure your button has a constructor that takes (x, y, width, height); (x, y); or (), as that's how your class will be instantiated on server-side (see ButtonPacket)
 * On client-side in GUIs
 * - add a button to the buttonList by instantiating this class and doing buttonList.add([...].button)
 * - override actionPerformed and use AbstractButtonWrapper#getWrapper to identify/get buttons and their wrappers, if need be
 * On server-side in TEs that extend IButtonTile
 * - implement IButtonTile#handleButtonPress and handle your button from there
 * - if you cannot guarantee this class will be instantiated before any button clicks are received, call AbstractButtonWrapper#registerWrapper (ideally in your TE init {} block)
 */
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

	/** Guaranteed to be non-null on client-side */
	open val button = if(SideUtils.isClient)
		WrappedGuiButton(x, y, width, height, this)
	else
		null

	/** Deserialize data coming from a network packet into possible class fields */
	open fun readExtraData(buf: ByteBuf) {}
	/** Serialize class fields into data to be transmitted over the network */
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
