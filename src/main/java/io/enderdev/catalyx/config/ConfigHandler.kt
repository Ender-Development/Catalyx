package io.enderdev.catalyx.config

import io.enderdev.catalyx.Catalyx
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class ConfigHandler<T : ConfigParser.ConfigItem>(
	private val configData: Array<String>, private val parser: (String) -> T
) {
	private val configItems: MutableList<T> = ArrayList()

	fun init(): ConfigHandler<T> {
		for(line in configData) {
			try {
				val entry = parser(line)
				configItems.add(entry)
			} catch(e: Exception) {
				Catalyx.logger.error("Error parsing config data: {}", line, e)
			}
		}
		return this
	}

	/**
	 * Check if the list contains the given item stack.
	 * @param stack The item stack to check.
	 * @return True if the list contains the item stack, false otherwise.
	 */
	fun contains(stack: ItemStack): Boolean {
		return configItems.any { it.compare(stack) }
	}

	/**
	 * Check if the player has any of the items in the list equipped.
	 * @param player The player to check.
	 * @return True if the player has any of the items equipped, false otherwise.
	 */
	fun equipped(player: EntityPlayer): Boolean {
		return player.equipmentAndArmor.any { contains(it) }
	}

	/**
	 * Get the first equipped config item that matches any of the items in the list.
	 * @param player The player to check.
	 * @return The first matching ConfigItem, or null if none found.
	 */
	fun getEquipped(player: EntityPlayer): ConfigParser.ConfigItem? {
		return player.equipmentAndArmor.firstOrNull { contains(it) }?.let { get(it) }
	}

	/**
	 * Get the first config item in the list that matches the given item stack.
	 * @param stack The item stack to check.
	 * @return The first matching ConfigItem, or null if none found.
	 */
	fun get(stack: ItemStack): ConfigParser.ConfigItem? {
		return configItems.firstOrNull { it.compare(stack) }
	}
}
