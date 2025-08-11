package io.enderdev.catalyx.tiles.helper

import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage

open class EnergyTileImpl(capacity: Int) : IEnergyTile {
	override val energyStorage = EnergyStorage(capacity)
	override val energyCapacity = capacity
}
