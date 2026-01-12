package org.ender_development.catalyx_.core.client.gui.wrappers

import net.minecraftforge.energy.IEnergyStorage

open class CapabilityEnergyDisplayWrapper(x: Int, y: Int, width: Int, height: Int, val energyStorage: () -> IEnergyStorage) : CapabilityDisplayWrapper(x, y, width, height) {
	override val stored: Int
		get() = energyStorage().energyStored

	override val capacity: Int
		get() = energyStorage().maxEnergyStored

	override val textLines: List<String>
		get() = listOf("${numFormat.format(stored)}/${numFormat.format(capacity)} FE")
}
