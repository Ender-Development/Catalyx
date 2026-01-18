package org.ender_development.catalyx.core.tiles

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.model.animation.CapabilityAnimation
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.client.button.AbstractButtonWrapper
import org.ender_development.catalyx.core.client.button.PauseButtonWrapper
import org.ender_development.catalyx.core.client.button.RedstoneButtonWrapper
import org.ender_development.catalyx.core.client.container.BaseContainer
import org.ender_development.catalyx.core.client.gui.BaseGuiTyped
import org.ender_development.catalyx.core.tiles.helper.*

/**
 * A base TileEntity in Catalyx, implementing separate input and output inventories; saving/loading from NBT; energy, fluid and item capability handling
 */
@Suppress("UNUSED")
abstract class BaseTile(open val mod: ICatalyxMod) : TileEntity(), BaseContainer.IBaseContainerCompat {
	var inputSlots = 0
	var outputSlots = 0
	override val inventorySlotCount
		get() = inventory.slots
	var dirtyTicks = 0

	lateinit var input: TileStackHandler
	protected lateinit var automationInput: IItemHandlerModifiable
	lateinit var output: TileStackHandler
	protected lateinit var automationOutput: IItemHandlerModifiable

	/**
	 * Whether the item capability is enabled for this tile.
	 * Can be overridden to disable item capability on certain tiles.
	 */
	open val enableItemCapability = true

	/**
	 * The facing of this tile, based on the [BlockHorizontal.FACING] property.
	 * Defaults to [EnumFacing.NORTH] if the property is not found.
	 */
	open val facing: EnumFacing
		get() = world.getBlockState(pos).properties.getOrDefault(BlockHorizontal.FACING, EnumFacing.NORTH) as EnumFacing

	open val inventory: IItemHandler
		get() = CombinedInvWrapper(input, output)

	open val automationInvHandler: CombinedInvWrapper
		get() = CombinedInvWrapper(automationInput, automationOutput)

	override fun canInteractWith(player: EntityPlayer) =
		!isInvalid && player.getDistanceSq(pos.add(.5, .5, .5)) <= 64

	override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState) =
		oldState.block !== newState.block

	fun initInventoryCapability(inputSlots: Int, outputSlots: Int) {
		this.inputSlots = inputSlots
		this.outputSlots = outputSlots

		initInventoryInputCapability()
		automationInput = object : WrappedItemHandler(input) {
			override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
		}

		output = object : TileStackHandler(outputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
		}

		automationOutput = object : WrappedItemHandler(output) {
			override fun extractItem(slot: Int, amount: Int, simulate: Boolean) =
				if(!getStackInSlot(slot).isEmpty) super.extractItem(slot, amount, simulate) else ItemStack.EMPTY
		}
	}

	open fun initInventoryInputCapability() {
		input = TileStackHandler(inputSlots, this)
	}

	override fun getUpdateTag() =
		writeToNBT(NBTTagCompound())

	override fun getUpdatePacket() =
		SPacketUpdateTileEntity(pos, 0, updateTag)

	override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) =
		readFromNBT(pkt.nbtCompound)

	open fun markDirtyClient() {
		markDirty()
		val state = world.getBlockState(getPos())
		world.notifyBlockUpdate(getPos(), state, state, 3)
	}

	open fun markDirtyClientEvery(ticks: Int) {
		dirtyTicks++
		if(dirtyTicks >= ticks) {
			markDirtyClient()
			dirtyTicks = 0
		}
	}

	open fun markDirtyEvery(ticks: Int) {
		dirtyTicks++
		if(dirtyTicks >= ticks) {
			markDirty()
			dirtyTicks = 0
		}
	}

	open fun markDirtyGUI() {
		markDirty()
		world?.let {
			val state = world.getBlockState(getPos())
			world.notifyBlockUpdate(pos, state, state, 6)
		}
	}

	open fun markDirtyGUIEvery(ticks: Int) {
		dirtyTicks++
		if(dirtyTicks >= ticks) {
			markDirtyGUI()
			dirtyTicks = 0
		}
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		if(this is IEnergyTile && compound.hasKey("EnergyStored")) {
			val energyStored = compound.getInteger("EnergyStored")
			energyStorage.extractEnergy(Int.MAX_VALUE, false)
			energyStorage.receiveEnergy(energyStored, false)
		}
		if(this is IItemTile) {
			if(compound.hasKey("input"))
				input.deserializeNBT(compound.getCompoundTag("input"))

			if(compound.hasKey("output"))
				output.deserializeNBT(compound.getCompoundTag("output"))
		}
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		if(this is IEnergyTile)
			compound.setInteger("EnergyStored", energyStorage.energyStored)
		if(this is IItemTile) {
			compound.setTag("input", input.serializeNBT())
			compound.setTag("output", output.serializeNBT())
		}
		return compound
	}

	open fun onBlockActivated(
		world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
		hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float
	): Boolean {
		if(this is IFluidTile) {
			val heldItem = player.getHeldItem(hand)
			if(heldItem.hasCapability(ITEM_FLUID_CAP, facing) || heldItem.hasCapability(FLUID_CAP, facing)) {
				val didInteract = FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)
				markDirty()
				return didInteract
			}
		}
		return false
	}

	/**
	 * WHATEVER IS GOING ON HERE; PLEASE DO NOT TOUCH ANY OF THIS; WE DON'T KNOW WHY IT WORKS; BUT IT DOES!
	 * SO IF SOMEONE COULD GIVE US SOME INSIGHT; PLEASE SEND HELP!!!
	 * ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
	 * ░░░░░░░░░░▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄░░░░░░░░░
	 * ░░░░░░░░▄▀░░░░░░░░░░░░▄░░░░░░░▀▄░░░░░░░
	 * ░░░░░░░░█░░▄░░░░▄░░░░░░░░░░░░░░█░░░░░░░
	 * ░░░░░░░░█░░░░░░░░░░░░▄█▄▄░░▄░░░█░▄▄▄░░░
	 * ░▄▄▄▄▄░░█░░░░░░▀░░░░▀█░░▀▄░░░░░█▀▀░██░░
	 * ░██▄▀██▄█░░░▄░░░░░░░██░░░░▀▀▀▀▀░░░░██░░
	 * ░░▀██▄▀██░░░░░░░░▀░██▀░░░░░░░░░░░░░▀██░
	 * ░░░░▀████░▀░░░░▄░░░██░░░▄█░░░░▄░▄█░░██░
	 * ░░░░░░░▀█░░░░▄░░░░░██░░░░▄░░░▄░░▄░░░██░
	 * ░░░░░░░▄█▄░░░░░░░░░░░▀▄░░▀▀▀▀▀▀▀▀░░▄▀░░
	 * ░░░░░░█▀▀█████████▀▀▀▀████████████▀░░░░
	 * ░░░░░░████▀░░███▀░░░░░░▀███░░▀██▀░░░░░░
	 * ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
	 */
	override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
		return if(capability == ITEM_CAP && !enableItemCapability)
			false
		else {
			when(capability) {
				ENERGY_CAP -> return this is IEnergyTile
				FLUID_CAP -> return this is IFluidTile
				ITEM_CAP -> return this is IItemTile
				ANIMATION_CAP -> return this is IAnimatedTile
			}
			super.hasCapability(capability, facing)
		}
	}

	/**
	 * WHATEVER IS GOING ON HERE; PLEASE DO NOT TOUCH ANY OF THIS; WE DON'T KNOW WHY IT WORKS; BUT IT DOES!
	 * SO IF SOMEONE COULD GIVE US SOME INSIGHT; PLEASE SEND HELP!!!
	 * ⣿⣿⣿⡿⢋⣍⠻⣿⣿⡿⢉⣍⢻⣿⠿⠿⠿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
	 * ⠿⠿⠿⠁⣾⣿⣆⣀⣠⣠⣿⣿⣦⠤⠶⢄⣀⣴⠀⠈⡙⠻⣿⣿⣿⣿⣿⣿
	 * ⣒⣒⠂⣸⣿⣉⣿⡋⠙⣿⣉⣹⣇⣐⣒⣻⣿⣿⣿⣿⣿⣦⡘⢿⣿⡿⠛⠛
	 * ⣿⣿⢠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡈⠛⠁⠄⢠
	 * ⣿⣿⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠘⣂⣴⣿
	 * ⣿⣿⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠇⣿⣿⣿⣿
	 * ⣿⣿⡈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⢠⣿⣿⣿⣿
	 * ⣿⣿⣷⣌⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⣠⣿⣿⣿⣿⣿
	 * ⣿⣿⣿⣿⣶⣀⣠⣤⣤⣄⣁⣤⣤⣤⣤⣄⣠⣤⣤⣄⣁⣼⣿⣿⣿⣿⣿⣿
	 */
	override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
		return if(capability == ITEM_CAP && !enableItemCapability)
			null
		else {
			when(capability) {
				ENERGY_CAP -> if(this is IEnergyTile) return ENERGY_CAP.cast<T>((this as IEnergyTile).energyStorage)
				FLUID_CAP -> if(this is IFluidTile) return FLUID_CAP.cast<T>(fluidHandler)
				ITEM_CAP -> if(this is IItemTile) return ITEM_CAP.cast<T>(automationInvHandler)
				ANIMATION_CAP -> if(this is IAnimatedTile) return ANIMATION_CAP.cast<T>(asm)
			}
			super.getCapability(capability, facing)
		}
	}

	init {
		// these classes don't get loaded on server-side by default, make sure they're registered instead of kicking the problem down to the mod creators
		if(this is BaseGuiTyped.IDefaultButtonVariables) {
			AbstractButtonWrapper.registerWrapper(PauseButtonWrapper::class.java)
			AbstractButtonWrapper.registerWrapper(RedstoneButtonWrapper::class.java)
		}
	}

	companion object {
		val ENERGY_CAP: Capability<IEnergyStorage> = CapabilityEnergy.ENERGY
		val ITEM_CAP: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
		val ITEM_FLUID_CAP: Capability<IFluidHandlerItem> = CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
		val FLUID_CAP: Capability<IFluidHandler> = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
		val ANIMATION_CAP: Capability<IAnimationStateMachine> = CapabilityAnimation.ANIMATION_CAPABILITY
	}
}
