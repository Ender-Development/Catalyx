package org.ender_development.catalyx.items

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.client.gui.BaseGuiTyped
import org.ender_development.catalyx.tiles.BaseTile
import org.ender_development.catalyx.tiles.helper.ICopyPasteExtraTile
import org.ender_development.catalyx.utils.DevUtils

class CopyPasteTool() : BaseItem(Catalyx, "copy_paste_tool") {
	private companion object {
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

		if(!copy && (copiedBlock.isEmpty() || copiedBlock != clickedBlockId))
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

			if(te is ICopyPasteExtraTile)
				te.copyData(copyTag)

			if(copyTag.isEmpty) // don't copy emptiness
				return EnumActionResult.PASS

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

			if(te is ICopyPasteExtraTile)
				te.pasteData(pasteTag, player)
		}

		if(!stack.hasTagCompound())
			stack.tagCompound = tag

		return EnumActionResult.SUCCESS
	}

	override fun addInformation(stack: ItemStack, world: World?, tooltip: List<String?>, flag: ITooltipFlag) {
		tooltip as MutableList
		tooltip.add("TODO ;p")

		if(DevUtils.isDeobfuscated) {
			tooltip.add("")
			tooltip.add("${stack.tagCompound?.getString(NBT_COPIED_BLOCK_KEY)}")
			tooltip.add("${stack.tagCompound?.getCompoundTag(NBT_COPIED_DATA_KEY)}")
		}
	}

	/**
	 * 	don't register if this isn't a dev environment, as this item is not finished
	 * 	TODO tooltip, name translation, maybe signify what blocks you can actually copy across ;p
	 * 	Question for roz: Why does this only work when using get() and not with a normal assignment?
	 * 	roz: because https://discord.com/channels/@me/1232745201009819749/1423296686691717220, hf
	 */
	override val isEnabled
		get() = DevUtils.isDeobfuscated
}
