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
import io.enderdev.catalyx.tiles.BaseTile
import io.enderdev.catalyx.tiles.helper.IGuiTile
import io.enderdev.catalyx.utils.RenderUtils
import io.enderdev.catalyx.utils.extensions.get
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation

abstract class BaseGuiTyped<T>(container: Container, val tileEntity: T) : GuiContainer(container) where T : IGuiTile, T : BaseTile, T : BaseGuiTyped.IDefaultButtonVariables {
	abstract val textureLocation: ResourceLocation

	val displayData = mutableListOf<CapabilityDisplayWrapper>()

	open var powerBarX = 0
	open var powerBarY = 0
	open val powerBarTexture = ResourceLocation(Reference.MODID, "textures/gui/container/gui.png")

	open val displayNameOffset = 8
	open val displayName: String = tileEntity.blockType.localizedName

	open val buttonSide = ButtonSide.RIGHT
	lateinit var pauseButton: PauseButton
	lateinit var redstoneButton: RedstoneButton

	init {
		xSize = tileEntity.guiWidth
		ySize = tileEntity.guiHeight
	}

	override fun initGui() {
		super.initGui()
		pauseButton = PauseButton(guiLeft + (if(buttonSide == ButtonSide.RIGHT) tileEntity.guiWidth - 16 else 0) + 4 * buttonSide.xMult, guiTop + (displayNameOffset shr 1))
		buttonList.add(pauseButton)
		redstoneButton = RedstoneButton(pauseButton.x + 18 * buttonSide.xMult, pauseButton.y)
		buttonList.add(redstoneButton)
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
		if(button is AbstractButton)
			PacketHandler.channel.sendToServer(ButtonPacket(tileEntity.pos, button))
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
		if(tileEntity.isPaused) pauseButton.isPaused = PauseButton.State.PAUSED
		else pauseButton.isPaused = PauseButton.State.RUNNING

		if(tileEntity.needsRedstonePower) redstoneButton.needsPower = RedstoneButton.State.ON
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
