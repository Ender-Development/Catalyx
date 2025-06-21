package io.enderdev.catalyx.client.gui

import io.enderdev.catalyx.Reference
import io.enderdev.catalyx.client.button.AbstractButton
import io.enderdev.catalyx.client.button.PauseButton
import io.enderdev.catalyx.client.button.RedstoneButton
import io.enderdev.catalyx.client.gui.wrappers.CapabilityDisplayWrapper
import io.enderdev.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.catalyx.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.catalyx.network.ButtonPacket
import io.enderdev.catalyx.network.PacketHandler
import io.enderdev.catalyx.tiles.BaseMachineTile
import io.enderdev.catalyx.tiles.helper.IGuiTile
import io.enderdev.catalyx.utils.RenderUtils
import io.enderdev.catalyx.utils.extensions.get
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation

abstract class BaseGui<T>(container: Container, val tile: T, val guiName: String) : GuiContainer(container) where T : BaseMachineTile<*>, T : IGuiTile {
	abstract val textureLocation: ResourceLocation

	val displayData = mutableListOf<CapabilityDisplayWrapper>()

	open var powerBarX = 0
	open var powerBarY = 0
	open val powerBarTexture = ResourceLocation(Reference.MODID, "textures/gui/container/gui.png")

	open val displayNameOffset = 8
	open val displayName = "tile.$guiName.name".translate()

	lateinit var pauseButton: PauseButton
	lateinit var redstoneButton: RedstoneButton

	init {
		xSize = tile.guiWidth
		ySize = tile.guiHeight
	}

	override fun initGui() {
		super.initGui()
		pauseButton = PauseButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4)
		this.buttonList.add(pauseButton)
		redstoneButton = RedstoneButton(this.guiLeft + 175 - 38, this.guiTop + displayNameOffset - 4)
		this.buttonList.add(redstoneButton)
	}

	open fun renderTooltips(mouseX: Int, mouseY: Int) {
		if(isHovered(pauseButton.x, pauseButton.y, 16, 16, mouseX, mouseY)) {
			if(tile.isPaused)
				this.drawHoveringText(listOf("tooltip.paused".translate()), mouseX, mouseY)
			else
				this.drawHoveringText(listOf("tooltip.running".translate()), mouseX, mouseY)
		}
		if(isHovered(redstoneButton.x, redstoneButton.y, 16, 16, mouseX, mouseY)) {
			if(tile.needsRedstonePower)
				this.drawHoveringText(listOf("tooltip.redstone_high".translate()), mouseX, mouseY)
			else
				this.drawHoveringText(listOf("tooltip.redstone_low".translate()), mouseX, mouseY)
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
		if(button is AbstractButton)
			PacketHandler.channel.sendToServer(ButtonPacket(tile.pos, button.buttonId))
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
		println(textureLocation)
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
		if(tile.isPaused) pauseButton.isPaused = PauseButton.State.PAUSED
		else pauseButton.isPaused = PauseButton.State.RUNNING

		if(tile.needsRedstonePower) redstoneButton.needsPower = RedstoneButton.State.ON
		else redstoneButton.needsPower = RedstoneButton.State.OFF

		if(this.displayName.isNotEmpty()) {
			this.fontRenderer.drawString(
				this.displayName,
				this.xSize / 2 - this.fontRenderer.getStringWidth(this.displayName) / 2,
				displayNameOffset,
				4210752 // that's the default minecraft container color
			)
		}
	}

	fun drawProgressBar(x: Int, y: Int, u: Int, v: Int, w: Int, h: Int) {
		mc.textureManager.bindTexture(textureLocation)
		val i = (width - xSize) shr 1
		val j = (height - ySize) shr 1
		if(tile.recipeTime == 0 && !tile.input[0].isEmpty) {
			drawTexturedModalRect(x + i, y + j, u, v, w, h)
		} else if(tile.progressTicks > 0) {
			val k = getBarScaled(w, tile.progressTicks, tile.recipeTime)
			drawTexturedModalRect(x + i, y + j, u, v, k, h)
		}
	}

	fun isHovered(x: Int, y: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) =
		mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
}
