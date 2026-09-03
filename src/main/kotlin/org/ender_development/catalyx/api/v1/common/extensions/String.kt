@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.api.v1.common.extensions

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient
import org.ender_development.catalyx.api.v1.utils.Utils
import org.ender_development.catalyx.core.utils.math.evaluator.Evaluator
import org.ender_development.catalyx.core.utils.math.evaluator.Expressions

inline fun String.toPotion(): Potion =
	Potion.getPotionFromResourceLocation(this)!!

inline fun String.toOre() =
	OreIngredient(this)

fun String.toResourceLocation(): ResourceLocation {
	val split = split(':')
	return if(split.size == 1) ResourceLocation(this) else ResourceLocation(split[0], split[1])
}

fun String.toStack(quantity: Int = 1, meta: Int = 0): ItemStack {
	val split = split(':')
	val meta = split.getApplyOrDefault(2, String::toInt) { meta }

	return toItem()?.toStack(quantity, meta) ?: toBlock()?.toStack(quantity, meta).orEmpty()
}

inline fun String.toItem(): Item? =
	Item.REGISTRY.registryObjects[toResourceLocation()]

inline fun String.toBlock(): Block? =
	Block.REGISTRY.registryObjects[toResourceLocation()]

fun String.toBlockState(meta: Int = 0): IBlockState? {
	val split = split(':')
	val meta = split.getApplyOrDefault(2, String::toInt) { meta }
	@Suppress("DEPRECATION")
	return toBlock()?.getStateFromMeta(meta)
}

inline fun String.toIngredient(meta: Int = 0): Ingredient =
	Ingredient.fromStacks(toStack(meta = meta))

inline fun String.toDict(prefix: String) =
	"$prefix${replaceFirstChar(Char::uppercaseChar)}"

inline fun String.firstOre(): ItemStack =
	OreDictionary.getOres(this).firstOrNull().orEmpty()

fun String.translate(vararg format: Any): String =
	if(Utils.environment.isServer)
		@Suppress("DEPRECATION")
		net.minecraft.util.text.translation.I18n.translateToLocalFormatted(this, *format)
	else
		I18n.format(this, *format)

inline fun String?.modLoaded(): Boolean =
	this != null && Loader.isModLoaded(this)

inline fun String.eval(): String =
	Expressions().evalToString(this)
