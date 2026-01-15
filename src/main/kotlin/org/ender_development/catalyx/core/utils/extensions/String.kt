@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils.extensions

import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient
import org.ender_development.catalyx.core.utils.SideUtils

inline fun String.toPotion(): Potion =
	Potion.getPotionFromResourceLocation(this)!!

inline fun String.toOre() =
	OreIngredient(this)

fun String.toStack(quantity: Int = 1, meta: Int = 0): ItemStack {
	val split = split(':')
	val meta = split.getApplyOrDefault(2, String::toInt) { meta }
	val location = if(split.size == 1) ResourceLocation(this) else ResourceLocation(split[0], split[1])

	return Item.REGISTRY.registryObjects[location]?.toStack(quantity, meta) ?: Block.REGISTRY.registryObjects[location]?.toStack(quantity, meta) ?: ItemStack.EMPTY
}

inline fun String.toIngredient(meta: Int = 0): Ingredient =
	Ingredient.fromStacks(toStack(meta = meta))

inline fun String.toDict(prefix: String) =
	"$prefix${replaceFirstChar(Char::uppercaseChar)}"

inline fun String.firstOre(): ItemStack =
	OreDictionary.getOres(this).firstOrNull() ?: ItemStack.EMPTY

fun String.translate(vararg format: Any): String =
	if(SideUtils.isServer)
		this
	else
		I18n.format(this, *format)

inline fun String?.modLoaded(): Boolean =
	this != null && Loader.isModLoaded(this)
