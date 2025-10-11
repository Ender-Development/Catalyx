package org.ender_development.catalyx.utils

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import java.io.IOException
import kotlin.io.path.createParentDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

open class PersistentData(
	// roz: this KDoc doesn't really display like I wanted it to, but oh well
	/**
	 * The unique id for your persistent data, has to be unique across all mods using Catalyx. Recommended is just your modId, or ${modId}_purpose if you're making more than one for some reason.
	 *
	 * Note: this **will be used as part of a filename**, as such, don't change this across mod versions and don't use any special characters.
	 */
	private val uniqueId: String
) {
	private val path = Loader.instance().configDir.toPath().resolve(Reference.MODID).resolve("persistent_data_$uniqueId.dat")
	private val data: NBTTagCompound by Delegates.lazyProperty @Synchronized {
		read()
	}

	val tag: NBTTagCompound
		get() = data

	@Synchronized
	fun save() =
		write()

	/**
	 * @return the read NBTTagCompound from disk
	 */
	private fun read(): NBTTagCompound {
		Catalyx.LOGGER.debug("Reading persistent data from path {}", path)

		if(path.notExists())
			return NBTTagCompound()

		return try {
			CompressedStreamTools.readCompressed(path.inputStream())
		} catch(e: IOException) {
			Catalyx.LOGGER.error("Failed to read persistent data (uniqueId: $uniqueId)", e)
			NBTTagCompound()
		}
	}

	/**
	 * @param tag the tag compound to save to disk
	 */
	private fun write() {
		Catalyx.LOGGER.debug("Write persistent data to path {}", path)

		if(data.isEmpty)
			return

		if(path.notExists())
			try {
				path.createParentDirectories()
			} catch(e: IOException) {
				Catalyx.LOGGER.error("Could not create persistent data dir", e)
				return
			}

		try {
			CompressedStreamTools.writeCompressed(tag, path.outputStream())
		} catch(e: IOException) {
			Catalyx.LOGGER.error("Failed to write persistent data (uniqueId: $uniqueId)", e)
		}
	}
}
