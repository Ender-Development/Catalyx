package io.enderdev.catalyx.config

import io.enderdev.catalyx.Catalyx
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class ConfigHandler<T : ConfigParser.ConfigItemStack>(configData: Iterable<String>, parser: (String) -> T) {
	private val configItems = mutableListOf<T>()

	init {
		try {
			configItems.addAll(configData.map(parser))
		} catch(e: Exception) {
			Catalyx.logger.error("Error parsing config data", e)
		}
	}

	/**
	 * Check if the list contains the given item stack.
	 * @param stack The item stack to check.
	 * @return True if the list contains the item stack, false otherwise.
	 */
	fun contains(stack: ItemStack) =
		configItems.any { it.compare(stack) }

	/**
	 * Check if the player has any of the items in the list equipped.
	 * @param player The player to check.
	 * @return True if the player has any of the items equipped, false otherwise.
	 */
	fun equipped(player: EntityPlayer) =
		player.equipmentAndArmor.any { contains(it) }

	/**
	 * Get the first equipped config item that matches any of the items in the list.
	 * @param player The player to check.
	 * @return The first matching ConfigItem, or null if none found.
	 */
	fun getEquipped(player: EntityPlayer) =
		player.equipmentAndArmor.firstOrNull { contains(it) }?.let { get(it) }

	/**
	 * Get the first config item in the list that matches the given item stack.
	 * @param stack The item stack to check.
	 * @return The first matching ConfigItem, or null if none found.
	 */
	operator fun get(stack: ItemStack) =
		configItems.firstOrNull { it.compare(stack) }
}
