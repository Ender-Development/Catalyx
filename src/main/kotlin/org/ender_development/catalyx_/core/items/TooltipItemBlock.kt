package org.ender_development.catalyx_.core.items

import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

private typealias ITooltipProvider = (stack: ItemStack, world: World?, flag: ITooltipFlag) -> Iterable<String>

/**
 * Use this class the same way as ItemBlock (i.e. you still need to set registryName)
 */
class TooltipItemBlock(block: Block, val tooltipProvider: ITooltipProvider) : ItemBlock(block) {
	constructor(block: Block, vararg tooltipLines: String) : this(block, { stack: ItemStack, world: World?, flag: ITooltipFlag -> tooltipLines.toList() })

	override fun addInformation(stack: ItemStack, world: World?, tooltip: List<String?>, flag: ITooltipFlag) {
		(tooltip as MutableList).addAll(tooltipProvider(stack, world, flag))
	}
}
