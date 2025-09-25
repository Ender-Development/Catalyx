package org.ender_development.catalyx.integration.top

import mcjty.theoneprobe.TheOneProbe
import mcjty.theoneprobe.api.*
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.tiles.helper.IFluidTile
import org.ender_development.catalyx.utils.extensions.getRealColor
import java.awt.Color

@Deprecated("We gonna switch to ModuleTheOneProbe eventually")
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
