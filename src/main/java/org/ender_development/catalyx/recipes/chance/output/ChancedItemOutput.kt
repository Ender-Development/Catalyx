package org.ender_development.catalyx.recipes.chance.output

import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import org.ender_development.catalyx.utils.NetworkUtils

class ChancedItemOutput(ingredient: ItemStack, chance: Int, boost: Int) : BoostableChancedOutput<ItemStack>(ingredient, chance, boost) {
	companion object {
		fun fromBuffer(buffer: PacketBuffer) =
			ChancedItemOutput(NetworkUtils.readItemStack(buffer), buffer.readVarInt(), buffer.readVarInt())

		fun toBuffer(buffer: PacketBuffer, output: ChancedItemOutput) {
			NetworkUtils.writeItemStack(buffer, output.ingredient)
			buffer.writeVarInt(output.chance)
			buffer.writeVarInt(output.boost)
		}
	}

	override fun copy() =
		ChancedItemOutput(ingredient.copy(), chance, boost)

	override fun toString(): String = "ChancedItemOutput{ingredient=ItemStack{item=${ingredient.item.registryName}, count=${ingredient.count}, meta=${ingredient.itemDamage}}, chance=$chance, boost=$boost}"
}
