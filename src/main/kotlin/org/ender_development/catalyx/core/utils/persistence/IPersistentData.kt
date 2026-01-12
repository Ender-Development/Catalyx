package org.ender_development.catalyx.core.utils.persistence

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

interface IPersistentData {
	/**
	 * An id unique across all other instances of the same type of persistent data
	 */
	val id: ResourceLocation

	/**
	 * The data stored with this persistent data
	 */
	val data: NBTTagCompound

	/**
	 * You're responsible for calling this to save your stored data
	 *
	 * Note: it's not guaranteed that a given implementation will save your data instantly after calling, and saving might be delayed to, for example, the next world save, or anything else.
	 */
	fun save()
}
