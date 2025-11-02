package org.ender_development.catalyx.utils.extensions

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.blocks.helper.RelativeDirection
import org.ender_development.catalyx.utils.math.BlockPosRotate.rotateY

val EnumFacing.glRotationAngle: Float
	get() = when(this) {
		EnumFacing.NORTH -> 180f
		EnumFacing.EAST -> 90f
		EnumFacing.SOUTH -> 0f
		EnumFacing.WEST -> -90f
		EnumFacing.UP, EnumFacing.DOWN -> 0f
	}

val EnumFacing.glOffsetX
	get() = when(this) {
		EnumFacing.NORTH, EnumFacing.SOUTH -> .5
		EnumFacing.WEST -> 1.0
		else -> .0
	}

val EnumFacing.glOffsetZ
	get() = when(this) {
		EnumFacing.NORTH -> 1.0
		EnumFacing.EAST, EnumFacing.WEST -> .5
		else -> .0
	}

@SideOnly(Side.CLIENT)
fun EnumFacing.glRotate() =
	when(this) {
		EnumFacing.NORTH -> GlStateManager.rotate(180f, 0f, 1f, 0f)
		EnumFacing.EAST -> GlStateManager.rotate(90f, 0f, 1f, 0f)
		EnumFacing.SOUTH -> {}
		EnumFacing.WEST -> GlStateManager.rotate(-90f, 0f, 1f, 0f)
		EnumFacing.UP -> GlStateManager.rotate(-90f, 1f, 0f, 0f)
		EnumFacing.DOWN -> GlStateManager.rotate(90f, 1f, 0f, 0f)
	}

/**
 * Gets the relative direction from this facing to the given facing.
 *
 * If any of the facings involved are vertical, [TOP][RelativeDirection.TOP]/[BOTTOM][RelativeDirection.BOTTOM] is returned
 *
 * @param facing The facing to get the relative direction to.
 * @return The [relative direction][RelativeDirection].
 */
fun EnumFacing.relativeDirectionTo(facing: EnumFacing) =
	if(this === EnumFacing.UP || facing === EnumFacing.UP)
		RelativeDirection.TOP
	else if(this === EnumFacing.DOWN || facing === EnumFacing.DOWN)
		RelativeDirection.BOTTOM
	else
		when(facing) {
			this -> RelativeDirection.FRONT
			opposite -> RelativeDirection.BACK
			rotateY() -> RelativeDirection.LEFT
			else -> RelativeDirection.RIGHT
		}
