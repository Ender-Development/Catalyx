package io.enderdev.catalyx.integration.top

import io.enderdev.catalyx.Reference
import io.enderdev.catalyx.tiles.helper.IFluidTile
import io.enderdev.catalyx.utils.extensions.getRealColor
import mcjty.theoneprobe.TheOneProbe
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import java.awt.Color
import kotlin.math.roundToInt

object CatalyxTOPHandler {
	internal fun init() {
		val top = TheOneProbe.theOneProbeImp
		top.registerProvider(object : IProbeInfoProvider {
			override fun getID() = "${Reference.MODID}.auto_provider"

			override fun addProbeInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData) {
				val tile = world.getTileEntity(data.pos)
				if(tile == null)
					return

				if(tile is IFluidTile)
					tile.fluidTanks?.let {
						it.tankProperties.forEach { tank ->
							val contents = tank.contents
							if(contents == null)
								info.progress(0, tank.capacity)
							else {
								val colour = contents.getRealColor()
								val altColour = Color(colour).darker().rgb
								// TODO for myself - finish this later
								info.progress((contents.amount / 1000.0).roundToInt(), tank.capacity / 1000, ProgressStyle().filledColor(colour).alternateFilledColor(altColour).borderColor(0).numberFormat(NumberFormat.COMPACT).suffix(" mB ${contents.fluid.name.replaceFirstChar(Char::uppercase)}"))
							}
						}
					}
			}
		})
	}
}
