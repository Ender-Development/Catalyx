package org.ender_development.catalyx.client.gui

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.client.button.AbstractButtonWrapper
import org.ender_development.catalyx.client.button.PauseButtonWrapper
import org.ender_development.catalyx.client.button.RedstoneButtonWrapper
import org.ender_development.catalyx.client.gui.wrappers.CapabilityDisplayWrapper
import org.ender_development.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import org.ender_development.catalyx.client.gui.wrappers.CapabilityFluidDisplayWrapper
import org.ender_development.catalyx.network.ButtonPacket
import org.ender_development.catalyx.network.PacketHandler
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.IGuiTile
import org.ender_development.catalyx_.core.utils.RenderAlignment
import org.ender_development.catalyx_.core.utils.RenderUtils
import org.ender_development.catalyx_.core.utils.extensions.get
import org.ender_development.catalyx_.core.utils.extensions.translate

// TODO fully rewrite this whole mess at some point
abstract class BaseGuiTyped<T>(container: Container, val tileEntity: T) : GuiContainer(container) where T : IGuiTile, T : BaseTile, T : BaseGuiTyped.IDefaultButtonVariables {
	abstract val textureLocation: ResourceLocation

	val displayData = mutableListOf<CapabilityDisplayWrapper>()

	open var powerBarX = 0
	open var powerBarY = 0
	open val powerBarTexture = ResourceLocation(Reference.MODID, "textures/gui/container/gui.png")

	open val displayNameOffset = 8
	open val displayName: String = tileEntity.blockType.localizedName
	open val displayNameAlignment = RenderAlignment(RenderAlignment.Alignment.TOP_MIDDLE)

	open val buttonAlignment = RenderAlignment(RenderAlignment.Alignment.TOP_RIGHT)
	lateinit var pauseButton: PauseButtonWrapper
	lateinit var redstoneButton: RedstoneButtonWrapper

	init {
		xSize = tileEntity.guiWidth
		ySize = tileEntity.guiHeight
	}

	override fun initGui() {
		super.initGui()
		val (x, y) = buttonAlignment.getXY(0, xSize, 0, ySize, 16 + 2 + 16, 16, 4, 4).let {
			// because the alignment here gets calculated for the entire gui height, and we have no way of checking which part of the GUI is the machine vs the inventory
			// thus the theoretical (for example) "bottom-left" is actually middle-left, but with a Y offset of 2 pixels up in the default rmt/alchem machines
			// hilarious honestly
			if(buttonAlignment.vertical == RenderAlignment.Vertical.MIDDLE)
				it.first to it.second - 2
			else
				it
		}
		redstoneButton = RedstoneButtonWrapper(guiLeft + x, guiTop + y)
		buttonList.add(redstoneButton.button)
		pauseButton = PauseButtonWrapper(redstoneButton.x + redstoneButton.width + 2, redstoneButton.y)
		buttonList.add(pauseButton.button)
	}

	open fun renderTooltips(mouseX: Int, mouseY: Int) {
		if(pauseButton.button!!.hovered)
			drawHoveringText(listOf("tooltip.${Reference.MODID}:${if(tileEntity.isPaused) "paused" else "running"}".translate()), mouseX, mouseY)

		if(redstoneButton.button!!.hovered)
			drawHoveringText(listOf("tooltip.${Reference.MODID}:redstone_${if(tileEntity.needsRedstonePower) "high" else "low"}".translate()), mouseX, mouseY)
	}

	open fun getBarScaled(pixels: Int, count: Int, max: Int) =
		if(count > 0 && max > 0)
			count * pixels / max
		else
			0

	open fun drawPowerBar(storage: CapabilityEnergyDisplayWrapper, texture: ResourceLocation, textureX: Int, textureY: Int) {
		val x = storage.x + guiLeft
		val y = storage.y + guiTop

		mc.textureManager.bindTexture(texture)
		drawTexturedModalRect(x, y, textureX, textureY, storage.width, storage.height)

		if(storage.stored > 5) {
			val barSize = getBarScaled(storage.height, storage.stored, storage.capacity)
			drawTexturedModalRect(x, y + storage.height - barSize, textureX + 16, textureY, storage.width, barSize)
		}

		mc.textureManager.bindTexture(textureLocation)
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		drawDefaultBackground()
		super.drawScreen(mouseX, mouseY, partialTicks)
		renderHoveredToolTip(mouseX, mouseY)
		renderTooltips(mouseX, mouseY)

		displayData.forEach {
			if(isHovered(it.x + guiLeft, it.y + guiTop, it.width, it.height, mouseX, mouseY))
				drawHoveringText(it.textLines, mouseX, mouseY, fontRenderer)
		}
	}

	override fun actionPerformed(button: GuiButton) {
		if(button is AbstractButtonWrapper.WrappedGuiButton)
			PacketHandler.channel.sendToServer(ButtonPacket(tileEntity.pos, button.wrapper))
	}

	fun drawFluidTank(wrapper: CapabilityFluidDisplayWrapper, x: Int, y: Int, width: Int = 16, height: Int = 70) {
		// draw the actual fluid texture
		if(wrapper.stored > 5) {
			RenderUtils.bindBlockTexture()
			RenderUtils.renderGuiTank(wrapper.fluid, wrapper.capacity, wrapper.stored, x.toDouble(), y.toDouble(), zLevel.toDouble(), width.toDouble(), height.toDouble())
		}

		// draw the empty tank overlay overtop
		val x = wrapper.x + guiLeft
		val y = wrapper.y + guiTop

		mc.textureManager.bindTexture(powerBarTexture)
		drawTexturedModalRect(x, y, 32, 0, width, height)
		mc.textureManager.bindTexture(textureLocation)
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		GlStateManager.color(1f, 1f, 1f, 1f)
		mc.textureManager.bindTexture(textureLocation)

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)

		displayData.forEach { data ->
			when(data) {
				is CapabilityEnergyDisplayWrapper -> drawPowerBar(data, powerBarTexture, powerBarX, powerBarY)
				is CapabilityFluidDisplayWrapper -> drawFluidTank(data, guiLeft + data.x, guiTop + data.y)
			}
		}
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		pauseButton.isPaused = if(tileEntity.isPaused)
			PauseButtonWrapper.State.PAUSED
		else
			PauseButtonWrapper.State.RUNNING

		redstoneButton.needsPower = if(tileEntity.needsRedstonePower)
			RedstoneButtonWrapper.State.ON
		else
			RedstoneButtonWrapper.State.OFF

		if(displayName.isNotEmpty()) {
			val (x, y) = displayNameAlignment.getXY(0, xSize, 0, ySize, fontRenderer.getStringWidth(displayName), fontRenderer.FONT_HEIGHT, displayNameOffset, displayNameOffset)
			fontRenderer.drawString(displayName, x, y, 0x404040)
		}
	}

	fun isHovered(x: Int, y: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) =
		mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height

	interface IDefaultButtonVariables {
		var isPaused: Boolean
		var needsRedstonePower: Boolean
	}
}

abstract class BaseGui(container: Container, tileEntity: BaseMachineTile<*>) : BaseGuiTyped<BaseMachineTile<*>>(container, tileEntity) {
	fun drawProgressBar(x: Int, y: Int, u: Int, v: Int, w: Int, h: Int) {
		mc.textureManager.bindTexture(textureLocation)
		if(tileEntity.recipeTime == 0 && !tileEntity.input[0].isEmpty) {
			drawTexturedModalRect(x + guiLeft, y + guiTop, u, v, w, h)
		} else if(tileEntity.progressTicks > 0) {
			val k = getBarScaled(w, tileEntity.progressTicks, tileEntity.recipeTime)
			drawTexturedModalRect(x + guiLeft, y + guiTop, u, v, k, h)
		}
	}
}
