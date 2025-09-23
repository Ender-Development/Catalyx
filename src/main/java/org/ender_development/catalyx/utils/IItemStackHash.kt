package org.ender_development.catalyx.utils

import it.unimi.dsi.fastutil.Hash
import net.minecraft.item.ItemStack
import java.util.*

/**
 * A configurable generator of hashing strategies, allowing for consideration of select properties of ItemStacks when
 * considering equality.
 */
interface IItemStackHash : Hash.Strategy<ItemStack> {
	companion object {
		/**
		 * @return a builder object for producing a custom ItemStackHashStrategy.
		 */
		val builder: Builder
			get() = Builder()

		/**
		 * Generates an ItemStackHash configured to compare every aspect of ItemStacks.
		 *
		 * @return the ItemStackHashStrategy as described above.
		 */
		val comparingAll: IItemStackHash
			get() = builder
				.compareItem()
				.compareMeta()
				.compareDamage()
				.compareNBT()
				.compareCount()
				.build()

		/**
		 * Generates an ItemStackHash configured to compare every aspect of ItemStacks except the number
		 * of items in the stack.
		 *
		 * @return the ItemStackHashStrategy as described above.
		 */
		val comparingAllButCount: IItemStackHash
			get() = builder
				.compareItem()
				.compareDamage()
				.compareNBT()
				.build()

		/**
		 * Generates an ItemStackHash configured to compare Item type and metadata only.
		 *
		 * @return the ItemStackHashStrategy as described above.
		 */
		val comparingItemDamageCount: IItemStackHash
			get() = builder
				.compareItem()
				.compareDamage()
				.compareCount()
				.build()
	}

	/**
	 * Builder pattern class for generating customized ItemStackHashStrategy
	 */
	class Builder {
		private var item = false
		private var meta = false
		private var damage = false
		private var nbt = false
		private var count = false

		/**
		 * Defines whether the Item type should be considered for equality.
		 *
		 * @param choice `true` to consider this property, `false` to ignore it.
		 * @return `this`
		 */
		fun compareItem(choice: Boolean = true): Builder {
			item = choice
			return this
		}

		/**
		 * Defines whether the Item metadata (damage value) should be considered for equality.
		 *
		 * @param choice `true` to consider this property, `false` to ignore it.
		 * @return `this`
		 */
		fun compareMeta(choice: Boolean = true): Builder {
			meta = choice
			return this
		}

		/**
		 * Defines whether the Item damage value should be considered for equality.
		 *
		 * @param choice `true` to consider this property, `false` to ignore it.
		 * @return `this`
		 */
		fun compareDamage(choice: Boolean = true): Builder {
			damage = choice
			return this
		}

		/**
		 * Defines whether the Item NBT data should be considered for equality.
		 *
		 * @param choice `true` to consider this property, `false` to ignore it.
		 * @return `this`
		 */
		fun compareNBT(choice: Boolean = true): Builder {
			nbt = choice
			return this
		}

		/**
		 * Defines whether stack size should be considered for equality.
		 *
		 * @param choice `true` to consider this property, `false` to ignore it.
		 * @return `this`
		 */
		fun compareCount(choice: Boolean = true): Builder {
			count = choice
			return this
		}

		/**
		 * @return the ItemStackHashStrategy as configured by "compare" methods.
		 */
		fun build(): IItemStackHash = object : IItemStackHash {
			override fun equals(a: ItemStack?, b: ItemStack?): Boolean {
				if(a == null || a.isEmpty) return b == null || b.isEmpty
				if(b == null || b.isEmpty) return false
				return (!item || a.item == b.item) &&
						(!meta || a.metadata == b.metadata) &&
						(!damage || a.itemDamage == b.itemDamage) &&
						(!nbt || Objects.equals(a.tagCompound, b.tagCompound)) &&
						(!count || a.count == b.count)
			}

			override fun hashCode(stack: ItemStack?): Int {
				if(stack == null || stack.isEmpty) return 0
				return Objects.hash(
					if(item) stack.item else null,
					if(meta) stack.metadata else null,
					if(damage) stack.itemDamage else null,
					if(nbt) stack.tagCompound else null,
					if(count) stack.count else null
				)
			}
		}
	}
}
