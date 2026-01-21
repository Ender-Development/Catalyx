package org.ender_development.catalyx.core.recipes.chance.output

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import org.ender_development.catalyx.api.v1.common.extensions.readItemStackOrEmpty

class ChancedItemOutput(ingredient: ItemStack, chance: Int, boost: Int) : BoostableChancedOutput<ItemStack>(ingredient, chance, boost) {
	companion object {
		fun fromBuffer(buffer: PacketBuffer) =
			ChancedItemOutput(buffer.readItemStackOrEmpty(), buffer.readVarInt(), buffer.readVarInt())

		fun toBuffer(buffer: PacketBuffer, output: ChancedItemOutput) {
			buffer.writeItemStack(output.ingredient)
			buffer.writeVarInt(output.chance)
			buffer.writeVarInt(output.boost)
		}
	}

	override fun copy() =
		ChancedItemOutput(ingredient.copy(), chance, boost)

	override fun toString() =
		"ChancedItemOutput{ingredient=ItemStack{item=${ingredient.item.registryName}, count=${ingredient.count}, meta=${ingredient.itemDamage}}, chance=$chance, boost=$boost}"
}
