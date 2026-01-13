package org.ender_development.catalyx.api.v1.interfaces.cast

import net.minecraft.util.ResourceLocation

/**
 * Implement this to provide capability to cast into [ResourceLocation]
 * no matter if you actually inhere from [ResourceLocation]
 */
interface IAsResourceLocation : Comparable<ResourceLocation> {

	override fun compareTo(other: ResourceLocation) = other.compareTo(this.asResourceLocation())

	fun asResourceLocation(): ResourceLocation
}
