package org.ender_development.catalyx.core.common.config

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.Catalyx

class ConfigHandler<T: GenericConfigEntry<*>>(configData: Iterable<String>, parser: (String) -> T) {
	private val configEntries = try {
	    configData.map { parser }
	} catch (e: Exception) {
		Catalyx.LOGGER.error("Error parsing config data", e)
		emptyList()
	}

	/**
	 * Check if the list contains the given input.
	 * @param stack The object to check.
	 * @return True if the list contains the item stack, false otherwise.
	 */
	fun contains(stack: Any) =
		configEntries.any { it == stack }

	/**
	 * Check if the player has any of the entries in the list equipped.
	 * @param player The player to check.
	 * @return True if the player has any of the entries equipped, false otherwise.
	 */
	fun equipped(player: EntityPlayer) =
		player.equipmentAndArmor.any(::contains)

	/**
	 * Get the first equipped config entry that matches any of the entries in the list.
	 * @param player The player to check.
	 * @return The first matching entry, or null if none found.
	 */
	fun getEquipped(player: EntityPlayer) =
		player.equipmentAndArmor.firstOrNull(::contains)?.let(::get)

	/**
	 * Get the first config entry in the list that matches the given item stack.
	 * @param stack The item stack to check.
	 * @return The first matching entry, or null if none found.
	 */
	operator fun get(stack: ItemStack) =
		configEntries.firstOrNull { it == stack }

}
