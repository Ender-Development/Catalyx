package io.enderdev.catalyx.utils.extensions

import net.minecraftforge.energy.IEnergyStorage

val IEnergyStorage.isFull: Boolean
	get() = maxEnergyStored == energyStored

val IEnergyStorage.isEmpty: Boolean
	get() = energyStored <= 0

val IEnergyStorage.emptySpace: Int
	get() = maxEnergyStored - energyStored
