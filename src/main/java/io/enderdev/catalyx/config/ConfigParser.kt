package io.enderdev.catalyx.config

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader

/**
 * Utility object for parsing configuration strings into item representations.
 * This object provides classes to handle items with different configurations,
 * including those with additional values.
 */
object ConfigParser {

	open class ConfigItem {
		protected var modid: String? = null
		protected var item: String? = null
		protected var meta: Int = -1

		/*
		 * Default constructor for ConfigItem.
		 * It initializes the object without any specific configuration.
		 * Use the constructor with a config string for actual item configuration.
		 * Parsing and validation need to be done in the specific constructors.
		 */
		constructor()

		/**
		 * Constructor that takes a configuration string in the format "modid:item:meta" or "modid:item".
		 * The meta value is optional and defaults to -1 if not provided.
		 * Should be used if config consists of a single item or list of items.
		 *
		 * @param configString The configuration string to parse.
		 */
		constructor(configString: String) {
			parseConfigString(configString)
			validateConfigItem()
		}

		open fun compare(obj: Any?): Boolean {
			return when(obj) {
				is ItemStack -> {
					if(meta == -1) {
						obj.isItemEqualIgnoreDurability(this.toItemStack())
					} else {
						obj.isItemEqual(this.toItemStack())
					}
				}
				is ConfigItem -> {
					this.modid == obj.modid && this.item == obj.item && this.meta == obj.meta
				}
				else -> super.equals(obj)
			}
		}

		open fun toItemStack(): ItemStack {
			val item = Item.getByNameOrId(ResourceLocation(modid!!, item!!).toString()) ?: throw NullPointerException("Item not found: $modid:$item")
			return ItemStack(item, 1, meta)
		}

		protected fun parseConfigString(configString: String) {
			val parts = configString.split(":")
			when(parts.size) {
				3 -> {
					modid = parts[0]
					item = parts[1]
					meta = parts[2].toInt()
				}
				2 -> {
					modid = parts[0]
					item = parts[1]
					meta = -1
				}
				else -> throw IllegalArgumentException("Invalid config string format: $configString")
			}
		}

		protected fun validateConfigItem() {
			if(modid == null || item == null) {
				throw IllegalArgumentException("Mod ID and item name cannot be null")
			}
			if(!Loader.isModLoaded(modid)) {
				throw IllegalArgumentException("Mod ID is not loaded: $modid")
			}
			if(meta < 0 && meta != -1) {
				throw IllegalArgumentException("Meta value cannot be negative")
			}
		}
	}

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemWithFloat(configString: String) : ConfigItem() {
		val value: Float

		init {
			val parts = configString.split(";")
			if(parts.size == 2) {
				parseConfigString(parts[0])
				value = parts[1].toFloat()
			} else {
				throw IllegalArgumentException("Invalid config string format: $configString")
			}
			validateConfigItem()
		}
	}

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemWithInt(configString: String) : ConfigItem() {
		val value: Int

		init {
			val parts = configString.split(";")
			if(parts.size == 2) {
				parseConfigString(parts[0])
				value = parts[1].toInt()
			} else {
				throw IllegalArgumentException("Invalid config string format: $configString")
			}
			validateConfigItem()
		}
	}

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemWithBoolean(configString: String) : ConfigItem() {
		val value: Boolean

		init {
			val parts = configString.split(";")
			if(parts.size == 2) {
				parseConfigString(parts[0])
				value = parts[1].toBoolean()
			} else {
				throw IllegalArgumentException("Invalid config string format: $configString")
			}
			validateConfigItem()
		}
	}
}

