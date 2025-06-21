package io.enderdev.catalyx.blocks

import net.minecraft.block.Block
import net.minecraftforge.event.RegistryEvent

interface IBlockProvider {
	fun registerBlock(event: RegistryEvent.Register<Block>)
}
