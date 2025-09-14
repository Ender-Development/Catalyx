package org.ender_development.catalyx.utils.extensions

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagLongArray
import net.minecraft.util.ReportedException
import org.ender_development.catalyx.recipes.ingredients.nbt.TagType

/**
 * Retrieves a long array using the specified key, or a zero-length array if no such key was stored.
 */
fun NBTTagCompound.getLongArray(key: String): LongArray {
	try {
		return if(hasKey(key, TagType.LONG_ARRAY.typeId))
			(tagMap[key] as NBTTagLongArray).data
		else
			LongArray(0)
	} catch(ex: ClassCastException) {
		throw ReportedException(createCrashReport(key, TagType.LONG_ARRAY.typeId, ex))
	}
}
