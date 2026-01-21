package org.ender_development.catalyx.core.recipes.chance.output

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.api.v1.common.extensions.readFluidStack
import org.ender_development.catalyx.api.v1.common.extensions.writeFluidStack
import org.ender_development.catalyx.api.v1.utils.Utils

class ChancedFluidOutput(ingredient: FluidStack, chance: Int, boost: Int) : BoostableChancedOutput<FluidStack>(ingredient, chance, boost) {
	companion object {
		fun fromBuffer(buffer: PacketBuffer) =
			ChancedFluidOutput(buffer.readFluidStack()!!, buffer.readVarInt(), buffer.readVarInt())

		fun toBuffer(buffer: PacketBuffer, output: ChancedFluidOutput) {
			buffer.writeFluidStack(output.ingredient)
			buffer.writeVarInt(output.chance)
			buffer.writeVarInt(output.boost)
		}
	}

	override fun copy() =
		ChancedFluidOutput(ingredient.copy(), chance, boost)

	override fun toString() =
		"ChancedFluidOutput{ingredient=FluidStack{fluid=${ingredient.unlocalizedName}, amount=${ingredient.amount}}, chance=$chance, boost=$chance}"
}
