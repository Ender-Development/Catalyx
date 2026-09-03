package org.ender_development.catalyx.api.v1.common.tileentities

import net.minecraftforge.energy.EnergyStorage
import org.ender_development.catalyx.api.v1.common.tileentities.interfaces.IEnergyTile

open class EnergyTileImpl(capacity: Int) : IEnergyTile {
	override val energyStorage = EnergyStorage(capacity)
	override val energyCapacity = capacity
}
