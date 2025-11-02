@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.utils.extensions

import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot

inline operator fun Container.get(slotId: Int): Slot =
	getSlot(slotId)
