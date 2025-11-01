package org.ender_development.catalyx.blocks.helper

import net.minecraftforge.common.property.IUnlistedProperty

class IOProperty(private val name: String): IUnlistedProperty<IOType> {
	override fun getName(): String = name

	override fun isValid(value: IOType) = IOType.entries.contains(value)

	override fun getType() = IOType::class.java

	override fun valueToString(value: IOType) = value.name
}
