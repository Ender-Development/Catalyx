package io.enderdev.catalyx.tiles.helper

import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage

open class EnergyTileImpl(private val capacity: Int) : IEnergyTile {
	override var energyStorage: IEnergyStorage = EnergyStorage(energyCapacity())

	final override fun energyCapacity() = capacity
}
