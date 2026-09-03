package org.ender_development.catalyx.api.v1.common.tileentities.interfaces

import net.minecraftforge.energy.IEnergyStorage

interface IEnergyTile {
	val energyStorage: IEnergyStorage
	val energyCapacity: Int
}
