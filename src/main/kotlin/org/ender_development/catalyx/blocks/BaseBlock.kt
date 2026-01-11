package org.ender_development.catalyx.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.core.IBlockProvider
import org.ender_development.catalyx.core.ICatalyxMod
import org.ender_development.catalyx.core.register
import org.ender_development.catalyx_.core.utils.SideUtils

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

	companion object {
		const val PIXEL_RATIO = 1.0 / 16.0
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
		// TODO: why do we have 2 init blocks?
		mod.register(this)
	}

	@Deprecated("")
	override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing) =
		blockAccess.getBlockState(pos.offset(side)).block !== this

	/**
	 * Gets the Axis-Aligned Bounding Box (AABB) for the edge block based on its state.
	 *
	 * @param state The block state of the edge block.
	 * @return The AABB of the edge block.
	 */
	open fun getAABB(state: IBlockState): AxisAlignedBB = FULL_BLOCK_AABB

	// We override these methods with a AABB check instead of hardcoding its return value
	@Deprecated("Implementation is fine.")
	override fun isFullCube(state: IBlockState): Boolean =
		getAABB(state) == FULL_BLOCK_AABB

	// We override these methods with a AABB check instead of hardcoding its return value
	@Deprecated("Implementation is fine.")
	override fun isFullBlock(state: IBlockState): Boolean =
		getAABB(state) == FULL_BLOCK_AABB

	// Doesn't change behavior as we default to FULL_BLOCK_AABB
	@Deprecated("Implementation is fine")
	override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB =
		getAABB(state)

	// Allow walls, fences, panes, etc. to connect based on AABB per face
	@Deprecated("Implementation is fine.")
	override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
		val aabb = getAABB(state)
		return when (face) {
			EnumFacing.UP -> if (aabb.maxY >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.DOWN -> if (aabb.minY <= 0.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.NORTH -> if (aabb.minZ <= 0.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.SOUTH -> if (aabb.maxZ >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.WEST -> if (aabb.minX <= 0.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
			EnumFacing.EAST -> if (aabb.maxX >= 1.0) BlockFaceShape.SOLID else BlockFaceShape.UNDEFINED
		}
	}
}
