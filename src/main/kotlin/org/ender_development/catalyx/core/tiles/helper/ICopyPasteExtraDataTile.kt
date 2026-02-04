package org.ender_development.catalyx.core.tiles.helper

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

// TODO move somewhere into API
/**
 * An interface for Tile Entities to implement if they want to copy/paste extra data with the Catalyx [CopyPasteTool][org.ender_development.catalyx.core.items.CopyPasteTool]
 */
interface ICopyPasteExtraDataTile {
	/**
	 * Write data into the NBT Tag to be copied and stored
	 *
	 * Note: if your TE implements [BaseGuiTyped.IDefaultButtonVariables][org.ender_development.catalyx.core.client.gui.BaseGuiTyped.IDefaultButtonVariables] (like [BaseMachineTile][org.ender_development.catalyx.core.tiles.BaseMachineTile] does), the `isPaused` and `needsRedstonePower` fields are already copied
	 */
	fun copyData(tag: NBTTagCompound)

	/**
	 * Load data from the NBT Tag into the TE state
	 *
	 * Note: take into account that there's no guarantee that all values that you wrote in `copyData` will be present in here, as the copy&paste tool's NBT data persists across mod updates and minecraft restarts
	 */
	fun pasteData(tag: NBTTagCompound, player: EntityPlayer)
}
