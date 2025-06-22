package io.enderdev.catalyx.client.gui

import io.enderdev.catalyx.client.container.BaseContainer
import io.enderdev.catalyx.tiles.BaseTile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class CatalyxGuiHandler : IGuiHandler {
	private val containers = mutableListOf<Class<out BaseContainer<*>>>()
	private val guis = mutableListOf<Class<out BaseGui<*>>>()
	private val tileEntities = mutableListOf<Class<out BaseTile>>()

	fun <T : BaseTile, C : BaseContainer<out T>, G : BaseGui<out T>> registerId(container: Class<out C>, gui: Class<out G>, te: Class<out T>): Int {
		containers.add(container)
		guis.add(gui)
		tileEntities.add(te)
		return tileEntities.size - 1
	}

	override fun getServerGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int) =
		getSidedGuiElement(containers, id, player, world, x, y, z)

	override fun getClientGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int) =
		getSidedGuiElement(guis, id, player, world, x, y, z)

	internal fun <T> getSidedGuiElement(sidedList: MutableList<Class<out T>>, id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
		if(player == null || world == null)
			return null

		val sidedGui = sidedList.getOrNull(id) ?: return null
		val tileEntity = tileEntities[id]

		val te = world.getTileEntity(BlockPos(x, y, z)) ?: return null
		if(te::class.java != tileEntity)
			return null

		return sidedGui.getConstructor(IInventory::class.java, tileEntity).newInstance(player.inventory, te)
	}
}
