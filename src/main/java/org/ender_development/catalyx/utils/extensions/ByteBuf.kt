@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.ByteBufUtils

/**
 * Writes the length of the string as well as the string itself
 */
inline fun ByteBuf.writeString(value: String) =
	ByteBufUtils.writeUTF8String(this, value)

/**
 * Reads the length of the string and the string itself
 */
inline fun ByteBuf.readString(): String =
	ByteBufUtils.readUTF8String(this)

inline fun ByteBuf.writeItemStack(stack: ItemStack) =
	ByteBufUtils.writeItemStack(this, stack)

inline fun ByteBuf.readItemStack(): ItemStack =
	ByteBufUtils.readItemStack(this)

inline fun ByteBuf.writeTagCompound(tag: NBTTagCompound) =
	ByteBufUtils.writeTag(this, tag)

inline fun ByteBuf.readTagCompound(): NBTTagCompound? =
	ByteBufUtils.readTag(this)
