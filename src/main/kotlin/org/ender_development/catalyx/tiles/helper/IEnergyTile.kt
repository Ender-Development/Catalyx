package org.ender_development.catalyx.tiles.helper

import net.minecraftforge.energy.IEnergyStorage

interface IEnergyTile {
	val energyStorage: IEnergyStorage
	val energyCapacity: Int
}
