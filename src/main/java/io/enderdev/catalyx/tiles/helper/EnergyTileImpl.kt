package io.enderdev.catalyx.tiles.helper

import net.minecraftforge.energy.EnergyStorage

open class EnergyTileImpl(capacity: Int) : IEnergyTile {
	override val energyStorage = EnergyStorage(capacity)
	override val energyCapacity = capacity
}
