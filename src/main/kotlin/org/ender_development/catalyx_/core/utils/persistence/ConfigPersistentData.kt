package org.ender_development.catalyx_.core.utils.persistence

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx_.core.Catalyx
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx_.core.utils.Delegates
import java.io.IOException
import kotlin.io.path.*

/**
 * A persistent data implementation that saves its data inside of the Catalyx config directory.
 */
open class ConfigPersistentData(override val id: ResourceLocation) : IPersistentData {
	private val path = Loader.instance().configDir.toPath() / Reference.MODID / "persistent_data_${id.namespace}-${id.path}.dat"

	override val data: NBTTagCompound by Delegates.lazyProperty(::read)

	@Synchronized
	private fun read(): NBTTagCompound {
		Catalyx.LOGGER.debug("Reading persistent data from path {}", path)

		if(path.notExists())
			return NBTTagCompound()

		return try {
			CompressedStreamTools.readCompressed(path.inputStream())
		} catch(e: IOException) {
			Catalyx.LOGGER.error("Failed to read persistent data (id: $id)", e)
			NBTTagCompound()
		}
	}

	@Synchronized
	override fun save() {
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
			CompressedStreamTools.writeCompressed(data, path.outputStream())
		} catch(e: IOException) {
			Catalyx.LOGGER.error("Failed to write persistent data (id: $id)", e)
		}
	}
}
