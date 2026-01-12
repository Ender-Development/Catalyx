package org.ender_development.catalyx_.core.tiles.helper

import net.minecraftforge.energy.IEnergyStorage

interface IEnergyTile {
	val energyStorage: IEnergyStorage
	val energyCapacity: Int
}
