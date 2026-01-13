package org.ender_development.catalyx.api.v1.interfaces.cast

import net.minecraft.util.ResourceLocation

/**
 * Implement this to provide capability to cast into [ResourceLocation]
 * no matter if you actually inhere from [ResourceLocation]
 *
 * @see [ICanCastInto]
 */
interface IAsResourceLocation :
	ICanCastInto<ResourceLocation>,
	Comparable<ResourceLocation> {

	override fun compareTo(other: ResourceLocation) = other.compareTo(this.asResourceLocation())

	override fun saveCast(): ResourceLocation = asResourceLocation()
	fun asResourceLocation(): ResourceLocation

	/**
	 * Converts a [ResourceLocation] into [IAsResourceLocation]
	 * It's a Duck-Typing workaround
	 *
	 * For example:
	 * ```
	 * fun giveMeAnyCastable(value: IAsResourceLocation)
	 *
	 * val myRL: ResourceLocation = ResourceLocation()
	 * giveMeAnyCastable(myRL) // does not work but
	 * giveMeAnyCastable(myRL.asIAsResourceLocation()) // does work
	 * ```
	 *
	 * But in practice, everywhere a parameter typed of [IAsResourceLocation] is awaited,
	 * an overloaded variant of the same function should be provided.
	 *
	 * ```
	 * fun giveMeAnyRL(value: IAsResourceLocation)
	 *      = giveMeAnyRL(value.asResourceLocation())
	 * fun giveMeAnyRL(value: ResourceLocation)
	 * ```
	 *
	 * @return an [IAsResourceLocation] wrapper for this [ResourceLocation]
	 */
	fun ResourceLocation.asIAsResourceLocation(): IAsResourceLocation = object : IAsResourceLocation {
		override fun asResourceLocation() = this@asIAsResourceLocation
	}
}
