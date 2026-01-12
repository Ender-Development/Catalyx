package org.ender_development.catalyx.core.utils.persistence

import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.Reference
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*

/**
 * A persistent data implementation that's only persistent across different worlds (i.e. actual worlds, not dimensions).
 *
 * Note: if there's no currently selected world (can happen for Singleplayer), [data] will be an empty [NBTTagCompound], see [wasLoaded] (note: the data is lazily loaded when accessed, so [wasLoaded] might be false even if the data will be loaded when [data] is read).
 *
 * When [autoLoad] is set, the data is automatically loaded during [net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent]; if this class is instantiated after the event occurs, the data will not be automatically loaded. This is intentional.
 *
 * After a given world is closed ([net.minecraftforge.fml.common.event.FMLServerStoppedEvent]) the data will be saved and cleared.
 *
 * The [onLoad] hook is called after the [data] has been loaded and is ready to be used; the [onUnload] hook is called right before the data is forcefully saved and unloaded.
 */
open class WorldPersistentData(override val id: ResourceLocation, val autoLoad: Boolean, val onLoad: () -> Unit, val onUnload: () -> Unit) : IPersistentData {
	override var data = NBTTagCompound()
		get() {
			read()
			return field
		}

	/**
	 * Will be true when the data gets loaded
	 */
	var wasLoaded = false
		private set

	@Synchronized
	private fun read() {
		if(wasLoaded)
			return

		val path = path ?: return

		Catalyx.LOGGER.debug("Reading persistent data from path {}", path)

		if(path.exists())
			try {
				data = CompressedStreamTools.readCompressed(path.inputStream())
			} catch(e: IOException) {
				Catalyx.LOGGER.error("Failed to read persistent data (id: $id)", e)
			}

		wasLoaded = true
		onLoad()
	}

	@Synchronized
	override fun save() {
		val path = path ?: return

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

	internal fun worldLeft() {
		onUnload()
		save()
		data.tagMap.clear()
		// this has to be last, otherwise the previous 2 calls would load the data again
		wasLoaded = false
	}

	internal fun worldJoined() {
		if(autoLoad)
			read()
	}

	init {
		instances.add(this)
		// scrapped idea becase it caused circular class instantiation when used improperly and thus a crash
		//if(autoLoad && ModuleManager.moduleStage >= ModuleStage.SERVER_ABOUT_TO_START && ModuleManager.moduleStage < ModuleStage.SERVER_STOPPED)
		//	read()
	}

	internal companion object {
		val instances = hashSetOf<WorldPersistentData>()
	}

	/** Nullable for client-side when [net.minecraft.server.integrated.IntegratedServer] hasn't been loaded yet */
	private val server: MinecraftServer?
		get() = FMLCommonHandler.instance().sidedDelegate.server

	private val path: Path?
		get() = server?.let { server -> server.anvilFile.toPath() / server.folderName / "data" / Reference.MODID / "persistent_data_${id.namespace}-${id.path}.dat" }
}
