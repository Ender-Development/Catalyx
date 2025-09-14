package org.ender_development.catalyx.recipes.chance.output

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.utils.NetworkUtils

class ChancedFluidOutput(ingredient: FluidStack, chance: Int, boost: Int) : BoostableChancedOutput<FluidStack>(ingredient, chance, boost) {
	companion object {
		fun fromBuffer(buffer: PacketBuffer) =
			ChancedFluidOutput(NetworkUtils.readFluidStack(buffer)!!, buffer.readVarInt(), buffer.readVarInt())

		fun toBuffer(buffer: PacketBuffer, output: ChancedFluidOutput) {
			NetworkUtils.writeFluidStack(buffer, output.ingredient)
			buffer.writeVarInt(output.chance)
			buffer.writeVarInt(output.boost)
		}
	}

	override fun copy() =
		ChancedFluidOutput(ingredient.copy(), chance, boost)

	override fun toString() =
		"ChancedFluidOutput{ingredient=FluidStack{fluid=${ingredient.unlocalizedName}, amount=${ingredient.amount}}, chance=$chance, boost=$chance}"
}
