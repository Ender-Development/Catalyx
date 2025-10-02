package org.ender_development.catalyx.config

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.utils.extensions.modLoaded

/**
 * Utility object for parsing configuration strings into item representations.
 * This object provides classes to handle items with different configurations,
 * including those with additional values.
 */
object ConfigParser {
	open class ConfigItemStack {
		companion object {
			internal const val IGNORE_META = -1
		}

		protected var modid: String? = null
		protected var itemid: String? = null
		protected var item: Item? = null
		protected var meta: Int = IGNORE_META

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

		open fun compare(other: Any?) =
			when(other) {
				is ItemStack ->
					if(meta == IGNORE_META)
						other.isItemEqualIgnoreDurability(toItemStack())
					else
						other.isItemEqual(toItemStack())
				is ConfigItemStack -> modid == other.modid && itemid == other.itemid && meta == other.meta
				else -> super.equals(other)
			}

		open fun toItemStack() =
			ItemStack(item ?: throw NullPointerException("Item not found: $modid:$itemid"), 1, meta)

		protected fun parseConfigString(configString: String) {
			val parts = configString.split(":")
			if(parts.size != 2 && parts.size != 3)
				throw IllegalArgumentException("Invalid config string format: $configString")

			modid = parts[0]
			itemid = parts[1]
			if(parts.size == 3)
				meta = parts[2].toInt()

			item = Item.getByNameOrId("$modid:$itemid") // check this in validateConfigItem?
		}

		protected fun validateConfigItem() {
			if(modid == null || itemid == null)
				throw IllegalArgumentException("Mod ID and item name cannot be null")

			if(!modid.modLoaded())
				throw IllegalArgumentException("Mod ID is not loaded: $modid")

			if(meta < IGNORE_META)
				throw IllegalArgumentException("Meta value cannot be negative")
		}
	}

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 *  @param parser The parser with which the value should be parsed to produce the expected value
	 */
	open class ConfigItemStackWith<T>(configString: String, parser: (String) -> T) : ConfigItemStack() {
		val value: T

		init {
			val parts = configString.split(';', ',')
			if(parts.size != 2)
				throw IllegalArgumentException("Invalid config string format: $configString")

			parseConfigString(parts[0])
			validateConfigItem()
			value = parser(parts[1])
		}
	}

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemStackWithFloat(configString: String) : ConfigItemStackWith<Float>(configString, String::toFloat)

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemStackWithInt(configString: String) : ConfigItemStackWith<Int>(configString, String::toInt)

	/**
	 *  Constructor that takes a configuration string in the format "modid:item:meta;value".
	 *
	 *  @param configString The configuration string to parse.
	 */
	class ConfigItemStackWithBoolean(configString: String) : ConfigItemStackWith<Boolean>(configString, String::toBoolean)
}

