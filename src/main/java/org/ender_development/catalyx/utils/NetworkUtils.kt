package org.ender_development.catalyx.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.Catalyx
import java.io.IOException

/**
 * Utility functions for reading and writing ItemStacks and FluidStacks to PacketBuffers.
 * Handles potential IOExceptions and logs them using the Catalyx logger.
 * Loosely based on code from [ModularUI](https://github.com/CleanroomMC/ModularUI/blob/master/src/main/java/com/cleanroommc/modularui/network/NetworkUtils.java) licensed under GNU LGPL-3.0
 */
object NetworkUtils {
	fun writeItemStack(buffer: PacketBuffer, itemStack: ItemStack): PacketBuffer = buffer.writeItemStack(itemStack)

	fun readItemStack(buffer: PacketBuffer): ItemStack {
		return try {
			buffer.readItemStack()
		} catch (e: IOException) {
			Catalyx.logger.catching(e)
			ItemStack.EMPTY
		}
	}

	fun writeFluidStack(buffer: PacketBuffer, fluidStack: FluidStack?) {
		if (fluidStack == null) {
			buffer.writeBoolean(true)
		} else {
			buffer.writeBoolean(false)
			var tag = fluidStack.writeToNBT(NBTTagCompound())
			buffer.writeCompoundTag(tag)
		}
	}

	fun readFluidStack(buffer: PacketBuffer): FluidStack? {
		return try {
			if (buffer.readBoolean()) null else FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag())
		} catch (e: IOException) {
			Catalyx.logger.catching(e)
			null
		}
	}
}
