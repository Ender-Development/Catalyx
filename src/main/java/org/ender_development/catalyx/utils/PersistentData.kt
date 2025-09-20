package org.ender_development.catalyx.utils

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.Reference
import org.jetbrains.annotations.ApiStatus
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object PersistentData {
	private var path: Path? = null
	private var tag: NBTTagCompound? = null

	@ApiStatus.Internal
	fun init() {
		path = Loader.instance().configDir.toPath().resolve(Reference.MODID).resolve("persistent_data.dat")
	}

	@Synchronized
	fun getTag(): NBTTagCompound {
		if(tag == null) tag = read()
		return tag!!
	}

	@Synchronized
	fun save() {
		if(tag != null) write(tag!!)
	}

	/**
	 * @return the read NBTTagCompound from disk
	 */
	private fun read(): NBTTagCompound {
		Catalyx.logger.debug("Reading persistent data from path $path")
		if(path == null) throw IllegalStateException("Persistent data path can not be null")
		if(!Files.exists(path!!)) return NBTTagCompound()
		try {
			val inputStream = Files.newInputStream(path!!)
			return CompressedStreamTools.readCompressed(inputStream)
		} catch(e: IOException) {
			Catalyx.logger.error("Failed to read persistent data", e)
			return NBTTagCompound()
		}
	}

	/**
	 * @param tag the tag compound to save to disk
	 */
	private fun write(tag: NBTTagCompound) {
		Catalyx.logger.debug("Write persistent data to path $path")
		if(tag.isEmpty) return
		if(path == null) throw IllegalStateException("Persistent data path can not be null")
		if(!Files.exists(path!!)) {
			try {
				Files.createDirectories(path!!.parent)
			} catch(e: IOException) {
				Catalyx.logger.error("Could not create persistent data dir", e)
				return
			}
		}

		try {
			val outputStream = Files.newOutputStream(path!!)
			CompressedStreamTools.writeCompressed(tag, outputStream)
		} catch(e: IOException) {
			Catalyx.logger.error("Failed to write persistent data", e)
		}
	}

}
