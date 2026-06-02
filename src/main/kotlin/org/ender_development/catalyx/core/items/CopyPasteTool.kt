package org.ender_development.catalyx.core.items

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.common.extensions.toBlock
import org.ender_development.catalyx.api.v1.common.extensions.translate
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.core.client.IAutoModel
import org.ender_development.catalyx.core.client.gui.BaseGuiTyped
import org.ender_development.catalyx.core.tiles.BaseTile
import org.ender_development.catalyx.core.tiles.helper.ICopyPasteExtraDataTile

class CopyPasteTool : BaseItem(Catalyx, "copy_paste_tool"), IAutoModel {
	companion object {
		const val NBT_COPIED_BLOCK_KEY = "CopiedBlock"
		const val NBT_COPIED_DATA_KEY = "CopiedData"
		const val NBT_IS_PAUSED_KEY = "IsPaused"
		const val NBT_NEEDS_REDSTONE_KEY = "NeedsRedstonePower"
	}

	override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
		val stack = player.getHeldItem(hand)
		val tag = stack.tagCompound ?: NBTTagCompound()
		val copiedBlock = tag.getString(NBT_COPIED_BLOCK_KEY)
		val clickedBlockId = world.getBlockState(pos).block.registryName!!.toString()
		val copy = player.isSneaking

		if(!copy && (copiedBlock.isBlank() || copiedBlock != clickedBlockId))
			return EnumActionResult.PASS

		val te = world.getTileEntity(pos)
		if(te !is BaseTile)
			return EnumActionResult.PASS

		if(copy) {
			val copyTag = NBTTagCompound()

			if(te is BaseGuiTyped.IDefaultButtonVariables) {
				copyTag.setBoolean(NBT_IS_PAUSED_KEY, te.isPaused)
				copyTag.setBoolean(NBT_NEEDS_REDSTONE_KEY, te.needsRedstonePower)
			}

			if(te is ICopyPasteExtraDataTile)
				te.copyData(copyTag)

			if(copyTag.isEmpty) { // don't copy emptiness
				player.sendMessage(TextComponentString("Couldn't copy anything from this block").setStyle(Style().setColor(TextFormatting.RED)))
				return EnumActionResult.PASS
			}

			tag.setTag(NBT_COPIED_DATA_KEY, copyTag)
			tag.setString(NBT_COPIED_BLOCK_KEY, clickedBlockId)
		} else { // paste
			val pasteTag = tag.getCompoundTag(NBT_COPIED_DATA_KEY)

			if(pasteTag.isEmpty) // should never happen
				return EnumActionResult.PASS

			if(te is BaseGuiTyped.IDefaultButtonVariables) {
				// these keys existing *should* be guaranteed, but checking never hurts
				if(pasteTag.hasKey(NBT_IS_PAUSED_KEY))
					te.isPaused = pasteTag.getBoolean(NBT_IS_PAUSED_KEY)

				if(pasteTag.hasKey(NBT_NEEDS_REDSTONE_KEY))
					te.needsRedstonePower = pasteTag.getBoolean(NBT_NEEDS_REDSTONE_KEY)
			}

			if(te is ICopyPasteExtraDataTile)
				te.pasteData(pasteTag, player)
		}

		if(!stack.hasTagCompound() && !tag.isEmpty)
			stack.tagCompound = tag

		return EnumActionResult.SUCCESS
	}

	override fun addInformation(stack: ItemStack, world: World?, tooltip: List<String?>, flag: ITooltipFlag) {
		tooltip as MutableList
		tooltip.add("$translationKey.desc.1".translate())
		tooltip.add("$translationKey.desc.2".translate())

		val tag = stack.tagCompound
		val copiedBlock = tag?.getString(NBT_COPIED_BLOCK_KEY)
		if(tag == null || copiedBlock.isNullOrBlank()) {
			tooltip.add("$translationKey.desc.empty".translate())
			return
		}

		val shift = GuiScreen.isShiftKeyDown()

		val block = copiedBlock.toBlock()
		tooltip.add("$translationKey.desc.copying".translate(if(shift || block == null) copiedBlock else block.localizedName))

		if(shift)
			tooltip.add(tag.getCompoundTag(NBT_COPIED_DATA_KEY).toString())
	}

	/**
	 * don't register if this isn't a dev environment, as this item is not finished
	 * TODO texture
	 */
	override fun isEnabled() =
		Utils.environment.isDeobfuscated

	override val textureLocation = ResourceLocation(Reference.MODID, "logo")
}
