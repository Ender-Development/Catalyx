package org.ender_development.catalyx.utils

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.utils.PersistentData.tag
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object PersistentData {
	private lateinit var path: Path
	private var data: NBTTagCompound? = null

	internal fun init() {
		path = Loader.instance().configDir.toPath().resolve(Reference.MODID).resolve("persistent_data.dat")
	}

	val tag: NBTTagCompound
		@Synchronized
		get() = data ?: read().also { data = it }

	@Synchronized
	fun save() =
		write()

	/**
	 * @return the read NBTTagCompound from disk
	 */
	private fun read(): NBTTagCompound {
		Catalyx.LOGGER.debug("Reading persistent data from path $path")

		if(!Files.exists(path))
			return NBTTagCompound()

		return try {
			CompressedStreamTools.readCompressed(Files.newInputStream(path))
		} catch(e: IOException) {
			Catalyx.LOGGER.error("Failed to read persistent data", e)
			NBTTagCompound()
		}
	}

	/**
	 * @param tag the tag compound to save to disk
	 */
	private fun write() {
		Catalyx.LOGGER.debug("Write persistent data to path $path")

		data?.let { data ->
			if(data.isEmpty)
				return

			if(!Files.exists(path))
				try {
					Files.createDirectories(path.parent)
				} catch(e: IOException) {
					Catalyx.LOGGER.error("Could not create persistent data dir", e)
					return
				}

			try {
				CompressedStreamTools.writeCompressed(tag, Files.newOutputStream(path))
			} catch(e: IOException) {
				Catalyx.LOGGER.error("Failed to write persistent data", e)
			}
		}
	}
}
