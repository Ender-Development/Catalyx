package org.ender_development.catalyx.tiles.helper

import net.minecraft.nbt.NBTTagCompound

/** TODO come up with a better name lmfao */
interface ICopyPasteExtraTile {
	/**
	 * Write data into the NBT Tag to be copied and stored
	 *
	 * Note: if your TE implements BaseGuiTyped.IDefaultButtonVariables (like [org.ender_development.catalyx.tiles.BaseMachineTile] does), the `isPaused` and `needsRedstonePower` fields are already copied
	 */
	fun copyData(tag: NBTTagCompound)

	/**
	 * Load data from the NBT Tag into the TE state
	 *
	 * Note: take into account that there's no guarantee that all values that you wrote in `copyData` will be present in here, as the copy&paste tool's NBT data persists across mod updates and minecraft restarts
	 */
	fun pasteData(tag: NBTTagCompound)
}
