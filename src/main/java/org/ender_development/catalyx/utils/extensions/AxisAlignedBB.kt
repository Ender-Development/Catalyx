package org.ender_development.catalyx.utils.extensions

import net.minecraft.util.math.AxisAlignedBB

/**
 * Rotates the bounding box around the Y axis by 90° clockwise the specified number of times.
 *
 * @param times The number of 90° clockwise rotations to apply.
 * @return A new [AxisAlignedBB] that has been rotated.
 */
fun AxisAlignedBB.rotateY(times: Int): AxisAlignedBB {
    // Normalize rotation to 0..3
    val steps = ((times % 4) + 4) % 4

    var minX = this.minX
    var maxX = this.maxX
    var minZ = this.minZ
    var maxZ = this.maxZ

    repeat(steps) {
        // 90° clockwise rotation: (x, z) → (1 - z, x)
        val newMinX = 1 - maxZ
        val newMaxX = 1 - minZ
        val newMinZ = minX
        val newMaxZ = maxX

        minX = newMinX
        maxX = newMaxX
        minZ = newMinZ
        maxZ = newMaxZ
    }

    return AxisAlignedBB(minX, this.minY, minZ, maxX, this.maxY, maxZ)
}
