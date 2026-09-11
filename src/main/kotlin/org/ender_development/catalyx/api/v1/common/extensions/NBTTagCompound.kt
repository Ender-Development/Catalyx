package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagLongArray
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.ReportedException
import org.ender_development.catalyx.api.v1.common.TagType
import java.util.regex.Pattern

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

/**
 * Retrieves a consistent string representation of an [NBTTagCompound].
 * This is needed for [org.ender_development.catalyx.api.v1.common.recipes.components.impl.ItemRecipeInput] and we can't use
 * the [NBTTagCompound.toString] method, as is only sorts keys in debug mode.
 */
fun NBTTagCompound.toSortedString(): String {
	if(isEmpty)
		return ""

	return tagMap.keys.sorted().joinToString(prefix = "{", postfix = "}") { key ->
		(if(key.any { !it.isLetterOrDigit() && it != '.' && it != '_' && it != '+' && it != '-' })
			NBTTagString.quoteAndEscape(key)
		else
			key) + ": " + tagMap[key]
	}
}
