package io.enderdev.catalyx

import io.enderdev.catalyx.blocks.BaseBlock
import io.enderdev.catalyx.items.BaseItem

internal object CatalyxRegistry {
	val blocks = mutableListOf<BaseBlock>()
	val items = mutableListOf<BaseItem>()
}
