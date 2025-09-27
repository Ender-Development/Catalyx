package org.ender_development.catalyx.utils

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.PersistentData.tag
import java.io.IOException
import kotlin.io.path.createParentDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

object PersistentData {
	private val path = Loader.instance().configDir.toPath().resolve(Reference.MODID).resolve("persistent_data.dat")
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
			Catalyx.LOGGER.error("Failed to read persistent data", e)
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
			Catalyx.LOGGER.error("Failed to write persistent data", e)
		}
	}
}
