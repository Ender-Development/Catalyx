package org.ender_development.catalyx.core.tiles

import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.core.client.tesr.AbstractTESRenderer
import org.ender_development.catalyx.core.client.tesr.HudInfoRenderer
import org.ender_development.catalyx.modules.coremodule.ICatalyxMod
import org.ender_development.catalyx.core.tiles.helper.HudInfoLine
import org.ender_development.catalyx.core.tiles.helper.IHudInfoProvider
import org.ender_development.catalyx.core.tiles.helper.ITESRTile
import org.ender_development.catalyx.core.utils.extensions.relativeDirectionTo
import org.ender_development.catalyx.core.utils.extensions.withAlpha
import java.awt.Color

open class TESRTile(mod: ICatalyxMod) : BaseTile(mod), ITESRTile, IHudInfoProvider {
	@SideOnly(Side.CLIENT)
	override val renderers: Array<out AbstractTESRenderer> = arrayOf(HudInfoRenderer)

	override fun getHudInfo(face: EnumFacing) =
		if(Minecraft.getMinecraft().player.isSneaking)
			arrayOf(HudInfoLine("Side: ${face.relativeDirectionTo(facing)} ($face)", Color.LIGHT_GRAY, Color.LIGHT_GRAY.withAlpha(.24f)))
		else
			emptyArray()
}
