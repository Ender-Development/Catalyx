package org.ender_development.catalyx.tiles

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.blocks.BaseRotatableTileBlock
import org.ender_development.catalyx.client.tesr.HudInfoRenderer
import org.ender_development.catalyx.client.tesr.IoRenderer
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.tiles.TesrTile.IOType.Companion.next
import org.ender_development.catalyx.tiles.helper.HudInfoLine
import org.ender_development.catalyx.tiles.helper.IHudInfoProvider
import org.ender_development.catalyx.tiles.helper.IPortRenderer
import org.ender_development.catalyx.tiles.helper.ITesr
import org.ender_development.catalyx.utils.extensions.withAlpha
import java.awt.Color
import java.util.Locale.getDefault

// TODO: Refactor this into a proper class with mutable IO sides and so on
open class TesrTile(mod: ICatalyxMod) : BaseTile(mod), ITesr, IHudInfoProvider, ITickable, IPortRenderer {
	enum class IOType(val value: String) : IStringSerializable {
		DEFAULT("default"), NONE("none"), INPUT("input"), PULL("pull"), OUTPUT("output"), PUSH("push");

		override fun getName(): String = value

		companion object {
			fun IOType.next(): IOType =
				entries[(this.ordinal + 1) % entries.size]
		}
	}

	val ioTOP = IOType.DEFAULT
	val ioBOTTOM = IOType.DEFAULT
	val ioFRONT = IOType.DEFAULT
	val ioBACK = IOType.DEFAULT
	val ioLEFT = IOType.DEFAULT
	val ioRIGHT = IOType.DEFAULT

	@SideOnly(Side.CLIENT)
	override fun getRenderers(): MutableList<TileEntitySpecialRenderer<BaseTile>> = mutableListOf(HudInfoRenderer, IoRenderer)

	override fun getHudInfo(face: EnumFacing?): List<HudInfoLine> =
		if((face != null) && Minecraft.getMinecraft().player.isSneaking) {
			listOf(HudInfoLine(Color.LIGHT_GRAY, Color.LIGHT_GRAY.withAlpha(.24f), "Side: ${getSideDirection(face).uppercase(getDefault())} (${face.toString().capitalize()})"))
		} else {
			emptyList()
		}

	override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
		getIOType(facing).next()
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ)
	}

	var counter = 0
	var displayCounter = 0.0
	override fun update() {
		if(world.isRemote) return
		counter++
		if(counter >= 50) {
			displayCounter += .1
			if(displayCounter >= 1.0) {
				displayCounter = 0.0
			}
			markDirty()
			counter = 0
		}
	}

	protected fun getSideDirection(face: EnumFacing) = when(facing) {
		face -> "front"
		face.opposite -> "back"
		face.rotateY() -> "right"
		face.rotateYCCW() -> "left"
		else -> "[error]"
	}

	val facing: EnumFacing
		get() {
			val state = this.getWorld().getBlockState(this.getPos())
			if(state.block is BaseRotatableTileBlock) {
				return state.getValue(BlockHorizontal.FACING)
			}
			return EnumFacing.NORTH
		}

	fun getIOType(side: EnumFacing): IOType =
		when(side) {
			EnumFacing.UP -> ioTOP.next()
			EnumFacing.DOWN -> ioBOTTOM.next()
			EnumFacing.NORTH -> ioFRONT.next()
			EnumFacing.SOUTH -> ioBACK.next()
			EnumFacing.WEST -> ioLEFT.next()
			EnumFacing.EAST -> ioRIGHT.next()
	}

	override fun getPortState(): Map<EnumFacing, IOType> =
		mapOf(
			EnumFacing.UP to ioTOP,
			EnumFacing.DOWN to ioBOTTOM,
			EnumFacing.NORTH to IOType.entries.random(),
			EnumFacing.SOUTH to IOType.entries.random(),
			EnumFacing.WEST to IOType.entries.random(),
			EnumFacing.EAST to IOType.entries.random()
		)
}
