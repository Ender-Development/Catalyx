package org.ender_development.catalyx.integration.top

import mcjty.theoneprobe.api.*
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.tiles.helper.IFluidTile
import org.ender_development.catalyx.utils.extensions.getRealColor
import org.ender_development.catalyx.utils.extensions.translate
import java.awt.Color

class FluidTileProvider : IProbeInfoProvider {
	override fun getID() = "${Reference.MODID}.auto.ifluidtile_provider"

	override fun addProbeInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData) {
		val tile = world.getTileEntity(data.pos) as? IFluidTile ?: return

		tile.fluidTanks.tankProperties.forEach { tank ->
			val contents = tank.contents
			if(contents != null && contents.amount > 0) {
				val color = contents.getRealColor()
				val altColour = Color(color).darker().rgb
				info.progress(
					contents.amount,
					tank.capacity,
					ProgressStyle().filledColor(color).alternateFilledColor(altColour).numberFormat(NumberFormat.COMMAS).borderColor(0)
						.suffix(" mB ${contents.fluid.unlocalizedName.translate()}")
				)
			}
		}
	}
}
