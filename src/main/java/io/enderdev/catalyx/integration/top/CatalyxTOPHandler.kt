package io.enderdev.catalyx.integration.top

import io.enderdev.catalyx.Reference
import io.enderdev.catalyx.tiles.helper.IFluidTile
import io.enderdev.catalyx.utils.extensions.getRealColor
import mcjty.theoneprobe.TheOneProbe
import mcjty.theoneprobe.api.*
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
					tile.fluidTanks.tankProperties.forEach { tank ->
						val contents = tank.contents
						if(contents != null && contents.amount > 0) {
							val colour = contents.getRealColor()
							val altColour = Color(colour).darker().rgb
							info.progress(contents.amount, tank.capacity, ProgressStyle().filledColor(colour).alternateFilledColor(altColour).numberFormat(NumberFormat.COMMAS).borderColor(0).suffix(" mB ${contents.fluid.name.replaceFirstChar(Char::uppercase)}"))
						}
					}
			}
		})
	}
}
