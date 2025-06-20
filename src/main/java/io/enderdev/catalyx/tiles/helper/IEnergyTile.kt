package io.enderdev.catalyx.tiles.helper

import net.minecraftforge.energy.IEnergyStorage

interface IEnergyTile {
	var energyStorage: IEnergyStorage
	fun energyCapacity(): Int
}
