package org.ender_development.catalyx.items

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent

object CatalyxModItems {
	internal val items = mutableListOf<IItemProvider>()

	val copyPasteTool = CopyPasteTool()

	fun registerItems(ev: RegistryEvent.Register<Item>) {
		println(items.joinToString { it.toString() }) // TODO remove for publication
		items.forEach { it.registerItem(ev) }
	}
}
