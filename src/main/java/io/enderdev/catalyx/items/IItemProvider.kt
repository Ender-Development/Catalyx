package io.enderdev.catalyx.items

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent

interface IItemProvider {
	fun registerItem(event: RegistryEvent.Register<Item>)
}
