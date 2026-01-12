package org.ender_development.catalyx.core.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.client.gui.CatalyxGuiHandler
import org.ender_development.catalyx.core.recipes.ingredients.nbt.TagType
import org.ender_development.catalyx.core.tiles.BaseTile

/**
 * A Catalyx Block interacting with a TileEntity and a GUI
 */
open class BaseTileBlock(mod: ICatalyxMod, name: String, val tileClass: Class<out TileEntity>, val guiId: Int) : BaseBlock(mod, name), ITileEntityProvider {
	/**
	 * Only use this constructor if you used a [org.ender_development.catalyx.client.gui.CatalyxGuiHandler] for the guiId
	 */
	constructor(mod: ICatalyxMod, name: String, guiId: Int) : this(mod, name, CatalyxGuiHandler.instances[mod]?.tileEntities[guiId] ?: error("Tried to use the BaseTileBlock constructor without a tileClass without using a CatalyxGuiHandler to register the GUI handler"), guiId)

	init {
		GameRegistry.registerTileEntity(tileClass, ResourceLocation(mod.modId, name))
	}

	override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity =
		tileClass.newInstance()

	override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		if(!world.isRemote) {
			val tile = world.getTileEntity(pos)
			if(tile is BaseTile && !tile.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ))
				player.openGui(mod, guiId, world, pos.x, pos.y, pos.z)
		}
		return true
	}

	override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean) =
		willHarvest || super.removedByPlayer(state, world, pos, player, false)

	override fun harvestBlock(world: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
		super.harvestBlock(world, player, pos, state, te, stack)
		world.setBlockToAir(pos)
	}

	override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
		val item = Item.getItemFromBlock(this)
		if(item === Items.AIR)
			return

		drops.add(ItemStack(item, 1, damageDropped(state)).apply {
			tagCompound = world.getTileEntity(pos)?.updateTag?.apply {
				arrayOf("x", "y", "z").forEach(::removeTag)
			}
		})

		val droppedItem: ItemStack? = drops.firstOrNull { it.item === item }
		val tag = droppedItem?.tagCompound
		tag?.apply {
			// remove if they exist
			arrayOf("id", "Owner").forEach { key ->
				if(hasKey(key))
					removeTag(key)
			}

			// remove if they exist and are 0
			arrayOf("ProgressTicks", "EnergyStored").forEach { key ->
				if(hasKey(key) && getInteger(key) == 0)
					removeTag(key)
			}

			// remove if empty
			arrayOf("input", "output").forEach { key ->
				if(hasKey(key)) {
					val io = getCompoundTag(key)
					if((io.hasKey("Size") && io.getInteger("Size") == 0) || io.getTagList("Items", TagType.COMPOUND.typeId).isEmpty)
						removeTag("input")
				}
			}

			// remove if empty
			arrayOf("InputTankNBT", "OutputTankNBT").forEach { key ->
				val tank = getCompoundTag(key)
				if(tank.hasKey("Empty"))
					removeTag(key)
			}

			if(isEmpty)
				droppedItem.tagCompound = null
		}
	}

	override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
		val tile = world.getTileEntity(pos)
		if(tile is BaseTile) {
			stack.tagCompound?.apply {
				setInteger("x", pos.x)
				setInteger("y", pos.y)
				setInteger("z", pos.z)
			}
			tile.markDirtyClient()
		}
	}
}
