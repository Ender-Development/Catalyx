package io.enderdev.catalyx.utils.extensions

import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient

fun String.toPotion(): Potion = Potion.getPotionFromResourceLocation(this)!!

fun String.toOre(): OreIngredient = OreIngredient(this)

fun String.toStack(quantity: Int = 1, meta: Int = 0): ItemStack {
	val split = split(':')
	val meta = split.getOrNull(2)?.toInt() ?: meta
	val location = if(split.size == 1) ResourceLocation(this) else ResourceLocation(split[0], split[1])

	Item.REGISTRY.getObject(location)?.apply { return toStack(quantity, meta) }

	val block: Block? = Block.REGISTRY.getObject(location)
	return if(block != null && block != Blocks.AIR && block != Blocks.WATER)
		block.toStack(quantity, meta)
	else
		ItemStack.EMPTY
}

fun String.toIngredient(quantity: Int = 1, meta: Int = 0): Ingredient = Ingredient.fromStacks(toStack(quantity, meta))

fun String.toDict(prefix: String) = "$prefix${replaceFirstChar(Char::uppercaseChar)}"

fun String.firstOre(): ItemStack = OreDictionary.getOres(this).firstOrNull() ?: ItemStack.EMPTY

val serverSide = FMLCommonHandler.instance().effectiveSide.isServer
fun String.translate(vararg format: Any): String = if(serverSide) this else I18n.format(this, *format)
