package org.ender_development.catalyx.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.IBlockProvider
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.register
import org.ender_development.catalyx.utils.SideUtils

/**
 * A base Catalyx Block
 */
open class BaseBlock(val mod: ICatalyxMod, name: String, material: Material = Material.ROCK, hardness: Float = 3f) : Block(material), IBlockProvider {
	init {
		registryName = ResourceLocation(mod.modId, name)
		translationKey = "$registryName"
		blockHardness = hardness
		creativeTab = mod.creativeTab
	}

	override val instance = this

	override var modDependencies = ""

	override val item = ItemBlock(this)

	override fun isEnabled() =
		true

	override fun register(event: RegistryEvent.Register<Block>) =
		event.registry.register(this)

	override fun registerItemBlock(event: RegistryEvent.Register<Item>) {
		item.registryName = registryName
		event.registry.register(item)
		if(SideUtils.isClient)
			ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(registryName!!, "inventory"))
	}

	override fun requires(modDependencies: String): Block {
		this.modDependencies = modDependencies
		mod.register(this)
		return this
	}

	init {
		mod.register(this)
	}

	@Deprecated("")
	override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing) =
		blockAccess.getBlockState(pos.offset(side)).block !== this
}
