package org.ender_development.catalyx.recipes.chance.output

import net.minecraft.network.PacketBuffer
import net.minecraftforge.fluids.FluidStack
import org.ender_development.catalyx.recipes.chance.IChance
import org.ender_development.catalyx.utils.NetworkUtils

class ChancedFluidOutput(ingredient: FluidStack, chance: Int, boost: Int): BoostableChancedOutput<FluidStack>(ingredient, chance, boost) {
	companion object {
		fun fromBuffer(buffer: PacketBuffer) = ChancedFluidOutput(NetworkUtils.readFluidStack(buffer)!!, buffer.readVarInt(), buffer.readVarInt())

		fun toBuffer(buffer: PacketBuffer, output: ChancedFluidOutput) {
			NetworkUtils.writeFluidStack(buffer, output.getIngredient())
			buffer.writeVarInt(output.getChance())
			buffer.writeVarInt(output.getBoost())
		}
	}

	override fun copy(): IChance<FluidStack> = ChancedFluidOutput(getIngredient().copy(), getChance(), getBoost())

	override fun toString(): String = "ChancedFluidOutput{ingredient=FluidStack{fluid=${getIngredient().unlocalizedName}, amount=${getIngredient().amount}}, chance=${getChance()}, boost=${getBoost()}}"
}
