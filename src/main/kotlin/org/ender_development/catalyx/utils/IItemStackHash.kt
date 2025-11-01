package org.ender_development.catalyx.utils

import it.unimi.dsi.fastutil.Hash
import net.minecraft.item.ItemStack
import java.util.*

/**
 * A configurable generator of hashing strategies, allowing for consideration of select properties of [ItemStack]s when
 * considering equality.
 */
interface IItemStackHash : Hash.Strategy<ItemStack> {
	companion object {
		/**
		 * @return a new [Builder] object for producing a custom [IItemStackHash] instance.
		 */
		val builder: Builder
			inline get() = Builder()

		/**
		 * Generates an [IItemStackHash] instance configured to compare every aspect of ItemStacks.
		 *
		 * @return a new [IItemStackHash] instance as described above.
		 */
		val comparingAll: IItemStackHash
			inline get() = builder.apply {
				item = true
				meta = true
				damage = true
				nbt = true
				amount = true
			}.build()

		/**
		 * Generates an [IItemStackHash] instance configured to compare every aspect of ItemStacks except the quantity and meta.
		 *
		 * @return a new [IItemStackHash] instance as described above.
		 */
		val comparingAllButCount: IItemStackHash
			inline get() = builder.apply {
				item = true
				damage = true
				nbt = true
			}.build()

		/**
		 * Generates an [IItemStackHash] instance configured to compare Item type and metadata only.
		 *
		 * @return a new [IItemStackHash] instance as described above.
		 */
		val comparingItemDamageCount: IItemStackHash
			inline get() = builder.apply {
				item = true
				damage = true
				amount = true
			}.build()
	}

	/**
	 * Builder pattern class for generating customized ItemStackHashStrategy
	 */
	class Builder {
		/** Whether the Item type should be considered for equality. */
		var item = false
		/** Whether the Item metadata (damage value) should be considered for equality. */
		var meta = false
		/** Whether the Item damage value should be considered for equality. */
		var damage = false
		/** Whether the Item NBT data should be considered for equality. */
		var nbt = false
		/** Whether the Item's amount (count/quantity) should be considered for equality. */
		var amount = false

		/**
		 * @return a new [IItemStackHash] instance as configured
		 */
		fun build(): IItemStackHash = object : IItemStackHash {
			override fun equals(a: ItemStack?, b: ItemStack?): Boolean {
				if(a?.isEmpty != false)
					return b?.isEmpty != false

				if(b?.isEmpty != false)
					return false

				return (!item || a.item === b.item) &&
						(!meta || a.metadata == b.metadata) &&
						(!damage || a.itemDamage == b.itemDamage) &&
						(!nbt || a.tagCompound == b.tagCompound) &&
						(!amount || a.count == b.count)
			}

			override fun hashCode(stack: ItemStack?): Int {
				if(stack == null || stack.isEmpty)
					return 0

				return Objects.hash(
					if(item) stack.item else null,
					if(meta) stack.metadata else null,
					if(damage) stack.itemDamage else null,
					if(nbt) stack.tagCompound else null,
					if(amount) stack.count else null
				)
			}
		}
	}
}
