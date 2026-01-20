package org.ender_development.catalyx.api.v1.utils.interfaces

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack

/**
 * Utility functions for reading and writing ItemStacks and FluidStacks to PacketBuffers.
 * Handles potential IOExceptions and logs them using the Catalyx logger.
 * Loosely based on code from [ModularUI](https://github.com/CleanroomMC/ModularUI/blob/master/src/main/java/com/cleanroommc/modularui/network/NetworkUtils.java) licensed under GNU LGPL-3.0
 */
interface INetworkUtils {
	fun writeItemStack(buffer: PacketBuffer, itemStack: ItemStack?): PacketBuffer
	fun readItemStack(buffer: PacketBuffer): ItemStack
	fun writeFluidStack(buffer: PacketBuffer, fluidStack: FluidStack?)
	fun readFluidStack(buffer: PacketBuffer): FluidStack?
}
