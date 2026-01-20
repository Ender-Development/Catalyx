package org.ender_development.catalyx.core.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.orEmpty
import org.ender_development.catalyx.api.v1.utils.interfaces.INetworkUtils
import java.io.IOException

/**
 * Utility functions for reading and writing ItemStacks and FluidStacks to PacketBuffers.
 * Handles potential IOExceptions and logs them using the Catalyx logger.
 * Loosely based on code from [ModularUI](https://github.com/CleanroomMC/ModularUI/blob/master/src/main/java/com/cleanroommc/modularui/network/NetworkUtils.java) licensed under GNU LGPL-3.0
 */
object NetworkUtils : INetworkUtils {
	override fun writeItemStack(buffer: PacketBuffer, itemStack: ItemStack?): PacketBuffer =
		buffer.writeItemStack(itemStack.orEmpty())

	override fun readItemStack(buffer: PacketBuffer): ItemStack =
		try {
			buffer.readItemStack()
		} catch(e: IOException) {
			Catalyx.LOGGER.catching(e)
			ItemStack.EMPTY
		}

	override fun writeFluidStack(buffer: PacketBuffer, fluidStack: FluidStack?) {
		buffer.writeBoolean(fluidStack == null)
		fluidStack?.let {
			buffer.writeCompoundTag(it.writeToNBT(NBTTagCompound()))
		}
	}

	override fun readFluidStack(buffer: PacketBuffer): FluidStack? =
		try {
			if(buffer.readBoolean())
				null
			else
				FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag())
		} catch(e: IOException) {
			Catalyx.LOGGER.catching(e)
			null
		}
}
