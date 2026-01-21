@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.Catalyx
import java.io.IOException

/*
 * Utility functions for reading and writing ItemStacss and FluidStacks to PacketBuffers.
 * Handles potential IOExceptions and logs them using the Catalyx logger.
 * Loosely based on code from [ModularUI](https://github.com/CleanroomMC/ModularUI/blob/master/src/main/java/com/cleanroommc/modularui/network/NetworkUtils.java) licensed under GNU LGPL-3.0
 */

inline fun PacketBuffer.writeItemStackOrEmpty(stack: ItemStack?) =
	writeItemStack(stack.orEmpty())

fun PacketBuffer.readItemStackOrEmpty(): ItemStack =
	try {
		readItemStack()
	} catch(e: IOException) {
		Catalyx.LOGGER.catching(e)
		ItemStack.EMPTY
	}

fun PacketBuffer.writeFluidStack(fluidStack: FluidStack?) {
	writeBoolean(fluidStack == null)
	fluidStack?.let {
		writeCompoundTag(it.writeToNBT(NBTTagCompound()))
	}
}

fun PacketBuffer.readFluidStack(): FluidStack? =
	try {
		if(readBoolean())
			null
		else
			FluidStack.loadFluidStackFromNBT(readCompoundTag())
	} catch(e: IOException) {
		Catalyx.LOGGER.catching(e)
		null
	}
