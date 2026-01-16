package org.ender_development.catalyx.core.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.utils.SideUtils

/**
 * A GUI handler you can use for your machines
 *
 * Use the return value of [registerId] in the [org.ender_development.catalyx.blocks.BaseTileBlock.guiId] field
 *
 * Remember to register it with [net.minecraftforge.fml.common.network.NetworkRegistry.registerGuiHandler]
 */
class CatalyxGuiHandler(mod: ICatalyxMod) : IGuiHandler {
	internal val containers = mutableListOf<Class<out Container>>()
	internal val guis = mutableListOf<Class<out GuiContainer>>()
	internal val tileEntities = mutableListOf<Class<out TileEntity>>()

	init {
		instances[mod] = this
	}

	fun registerId(te: Class<out TileEntity>, container: Class<out Container>, gui: () -> Class<out GuiContainer>): Int {
		tileEntities.add(te)
		containers.add(container)
		if(SideUtils.isClient)
			guis.add(gui())
		return tileEntities.size - 1
	}

	override fun getServerGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int) =
		getSidedGuiElement(containers, id, player, world, x, y, z)

	override fun getClientGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int) =
		getSidedGuiElement(guis, id, player, world, x, y, z)

	private fun <T> getSidedGuiElement(sidedList: MutableList<Class<out T>>, id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
		if(player == null || world == null)
			return null

		val sidedGui = sidedList.getOrNull(id) ?: return null
		val tileEntity = tileEntities[id]

		val te = world.getTileEntity(BlockPos(x, y, z)) ?: return null
		if(te::class.java != tileEntity)
			return null

		return sidedGui.getConstructor(IInventory::class.java, tileEntity).newInstance(player.inventory, te)
	}

	internal companion object {
		val instances = mutableMapOf<ICatalyxMod, CatalyxGuiHandler>()
	}
}
