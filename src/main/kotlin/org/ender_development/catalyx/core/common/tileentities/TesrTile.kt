package org.ender_development.catalyx.core.common.tileentities

import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.api.v1.common.extensions.relativeDirectionTo
import org.ender_development.catalyx.api.v1.common.extensions.withAlpha
import org.ender_development.catalyx.api.v1.ICatalyxMod
import org.ender_development.catalyx.core.client.tesr.AbstractTESRenderer
import org.ender_development.catalyx.core.client.tesr.HudInfoRenderer
import org.ender_development.catalyx.api.v1.common.tileentities.interfaces.HudInfoLine
import org.ender_development.catalyx.api.v1.common.tileentities.interfaces.IHudInfoProvider
import org.ender_development.catalyx.api.v1.common.tileentities.interfaces.ITESRTile
import java.awt.Color

open class TesrTile(mod: ICatalyxMod) : BaseTile(mod), ITESRTile, IHudInfoProvider {
	@SideOnly(Side.CLIENT)
	override val renderers: Array<out AbstractTESRenderer> = arrayOf(HudInfoRenderer)

	override fun getHudInfo(face: EnumFacing) =
		if(Minecraft.getMinecraft().player.isSneaking)
			arrayOf(HudInfoLine("Side: ${face.relativeDirectionTo(facing)} ($face)", Color.LIGHT_GRAY, Color.LIGHT_GRAY.withAlpha(.24f)))
		else
			emptyArray()
}
