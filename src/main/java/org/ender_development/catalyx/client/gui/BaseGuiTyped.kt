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
import org.ender_development.catalyx.utils.RenderAlignment
import org.ender_development.catalyx.utils.RenderUtils
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.translate

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
		pauseButton = PauseButtonWrapper(redstoneButton.x + 16 + 2, redstoneButton.y)
		buttonList.add(pauseButton.button)
	}

	open fun renderTooltips(mouseX: Int, mouseY: Int) {
		if(isHovered(pauseButton.x, pauseButton.y, 16, 16, mouseX, mouseY)) {
			if(tileEntity.isPaused)
				this.drawHoveringText(listOf("tooltip.${Reference.MODID}.paused".translate()), mouseX, mouseY)
			else
				this.drawHoveringText(listOf("tooltip.${Reference.MODID}.running".translate()), mouseX, mouseY)
		}
		if(isHovered(redstoneButton.x, redstoneButton.y, 16, 16, mouseX, mouseY)) {
			if(tileEntity.needsRedstonePower)
				this.drawHoveringText(listOf("tooltip.${Reference.MODID}.redstone_high".translate()), mouseX, mouseY)
			else
				this.drawHoveringText(listOf("tooltip.${Reference.MODID}.redstone_low".translate()), mouseX, mouseY)
		}
	}

	open fun getBarScaled(pixels: Int, count: Int, max: Int): Int {
		return if(count > 0 && max > 0) count * pixels / max else 0
	}

	open fun drawPowerBar(
		storage: CapabilityEnergyDisplayWrapper,
		texture: ResourceLocation,
		textureX: Int,
		textureY: Int
	) {
		val i = storage.x + ((this.width - this.xSize) / 2)
		val j = storage.y + ((this.height - this.ySize) / 2)
		val k = this.getBarScaled(storage.height, storage.getStored(), storage.getCapacity())
		mc.textureManager.bindTexture(texture)
		this.drawTexturedModalRect(i, j, textureX, textureY, storage.width, storage.height)
		if(storage.getStored() > 0) {
			this.drawTexturedModalRect(i, j + storage.height - k, textureX + 16, textureY, storage.width, k)
		}
		this.mc.textureManager.bindTexture(this.textureLocation)
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		drawDefaultBackground()
		super.drawScreen(mouseX, mouseY, partialTicks)
		renderHoveredToolTip(mouseX, mouseY)
		renderTooltips(mouseX, mouseY)

		val x = (this.width - this.xSize) / 2
		val y = (this.height - this.ySize) / 2
		this.displayData.filter { data ->
			(mouseX >= data.x + x
					&& mouseX <= data.x + x + data.width
					&& mouseY >= data.y + y
					&& mouseY <= data.y + y + data.height)
		}.forEach { drawHoveringText(it.toStringList(), mouseX, mouseY, fontRenderer) }
	}

	override fun actionPerformed(button: GuiButton) {
		if(button is AbstractButtonWrapper.WrappedGuiButton)
			PacketHandler.channel.sendToServer(ButtonPacket(tileEntity.pos, button.wrapper))
	}

	fun drawFluidTank(wrapper: CapabilityFluidDisplayWrapper, i: Int, j: Int, width: Int = 16, height: Int = 70) {
		if(wrapper.getStored() > 5) {
			RenderUtils.bindBlockTexture()
			RenderUtils.renderGuiTank(
				wrapper.getFluid(), wrapper.getCapacity(),
				wrapper.getStored(), i.toDouble(), j.toDouble(), zLevel.toDouble(), width.toDouble(), height.toDouble()
			)
		}
		val i = wrapper.x + ((this.width - this.xSize) / 2)
		val j = wrapper.y + ((this.height - this.ySize) / 2)
		mc.textureManager.bindTexture(powerBarTexture)
		this.drawTexturedModalRect(i, j, 32, 0, 16, 70)
		this.mc.textureManager.bindTexture(this.textureLocation)
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
		this.mc.textureManager.bindTexture(this.textureLocation)

		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize)
		val i = (this.width - this.xSize) / 2
		val j = (this.height - this.ySize) / 2

		displayData.forEach { data ->
			when(data) {
				is CapabilityEnergyDisplayWrapper -> drawPowerBar(data, powerBarTexture, powerBarX, powerBarY)
				is CapabilityFluidDisplayWrapper -> drawFluidTank(data, i + data.x, j + data.y)
			}
		}
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		if(tileEntity.isPaused) pauseButton.isPaused = PauseButtonWrapper.State.PAUSED
		else pauseButton.isPaused = PauseButtonWrapper.State.RUNNING

		if(tileEntity.needsRedstonePower) redstoneButton.needsPower = RedstoneButtonWrapper.State.ON
		else redstoneButton.needsPower = RedstoneButtonWrapper.State.OFF

		if(displayName.isNotEmpty()) {
			val (x, y) = displayNameAlignment.getXY(0, xSize, 0, ySize, fontRenderer.getStringWidth(displayName), fontRenderer.FONT_HEIGHT, displayNameOffset, displayNameOffset)
			fontRenderer.drawString(displayName, x, y, 4210752)
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
		val i = (width - xSize) shr 1
		val j = (height - ySize) shr 1
		if(tileEntity.recipeTime == 0 && !tileEntity.input[0].isEmpty) {
			drawTexturedModalRect(x + i, y + j, u, v, w, h)
		} else if(tileEntity.progressTicks > 0) {
			val k = getBarScaled(w, tileEntity.progressTicks, tileEntity.recipeTime)
			drawTexturedModalRect(x + i, y + j, u, v, k, h)
		}
	}
}
