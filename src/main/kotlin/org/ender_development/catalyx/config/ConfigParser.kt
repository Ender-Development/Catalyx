package org.ender_development.catalyx.config

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.utils.extensions.modLoaded
import java.util.*

/**
 * Utility object for parsing configuration strings into different representations.
 *
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

	open class ConfigBlockState() {
		companion object {
			internal const val IGNORE_META = -1
		}

		private var modId: String? = null
		private var blockId: String? = null
		private var meta: Int = IGNORE_META

		open val block: Block?
			get() {
				val id = ResourceLocation(modId ?: return null, blockId ?: return null)
				if(!Block.REGISTRY.containsKey(id))
					return null
				return Block.REGISTRY.getObject(id)
			}

		open val state: IBlockState?
			@Suppress("DEPRECATION")
			get() = block?.getStateFromMeta(if(meta == IGNORE_META) 0 else meta)

		/**
		 * Constructor that takes a configuration string in the format "modId:blockId:meta" or "modId:blockId"
		 * Meta defaults to [IGNORE_META] if not set
		 * Should be used if config consists of a single block or list of blocks.
		 *
		 * @param configString The configuration string to parse.
		 */
		constructor(configString: String) : this() {
			parseConfigString(configString)
			validate()
		}

		override fun equals(other: Any?) =
			when {
				this === other -> true
				other is IBlockState -> block === other.block && (meta == IGNORE_META || block?.getMetaFromState(other) == meta)
				other is ConfigBlockState -> modId == other.modId && blockId == other.blockId && meta == other.meta
				else -> false
			}

		override fun hashCode() =
			Objects.hash(modId, blockId, meta)

		open fun parseConfigString(configString: String) {
			val parts = configString.split(":")
			if(parts.size != 2 && parts.size != 3)
				error("Invalid config string format: '$configString'")

			modId = parts[0]
			blockId = parts[1]
			if(parts.size == 3)
				meta = parts[2].toInt()
		}

		open fun validate() {
			if(modId == null || blockId == null)
				error("Mod Id and Block Id cannot be null")

			if(!modId.modLoaded())
				error("Mod ID is not loaded: $modId")

			if(meta < IGNORE_META)
				error("Meta value cannot be negative")
		}
	}

	/**
	 * Helper class
	 *
	 * @param configString The configuration string to parse in the format "modId:blockId:meta;value"
	 * @param optional Whether the argument is optional or not
	 * @param parser The parser with which the value should be parsed to produce the expected value
	 */
	open class ConfigBlockStateWith<T : Any>(configString: String, optional: Boolean, parser: (String) -> T) : ConfigBlockState() {
		/**
		 * Only nullable when [optional] is true
		 */
		val value: T?

		init {
			val parts = configString.split(';', ',')
			if(parts.size == 2) {
				parseConfigString(parts[0])
				value = parser(parts[1])
			} else {
				if(!optional)
					error("Invalid config string format: '$configString'")
				else {
					parseConfigString(configString)
					value = null
				}
			}
			validate()
		}
	}

	class ConfigBlockStateWithInt(configString: String, optional: Boolean) : ConfigBlockStateWith<Int>(configString, optional, String::toInt)

	class ConfigBlockStateWithFloat(configString: String, optional: Boolean) : ConfigBlockStateWith<Float>(configString, optional, String::toFloat)

	class ConfigBlockStateWithBoolean(configString: String, optional: Boolean) : ConfigBlockStateWith<Boolean>(configString, optional, String::toBoolean)
}

