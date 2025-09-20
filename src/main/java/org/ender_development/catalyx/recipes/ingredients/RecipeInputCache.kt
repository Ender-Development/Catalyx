package org.ender_development.catalyx.recipes.ingredients

import it.unimi.dsi.fastutil.Hash
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.MathHelper
import org.ender_development.catalyx.utils.PersistentData
import org.jetbrains.annotations.ApiStatus
import java.util.*
import kotlin.math.max

object RecipeInputCache {
	const val MINIMUM_CACHE_SIZE = 1 shl 13
	const val MAXIMUM_CACHE_SIZE = 1 shl 30

	const val DATA_NAME = "expectedIngredientInstances"

	var instances: ObjectOpenHashSet<RecipeInput>? = null

	fun isCacheEnabled(): Boolean =
		instances != null

	private fun getExpectedInstanceAmount(tagCompound: NBTTagCompound): Int =
		MathHelper.clamp(tagCompound.getInteger(DATA_NAME), MINIMUM_CACHE_SIZE, MAXIMUM_CACHE_SIZE)

	@ApiStatus.Internal
	fun enableCache() {
		if(isCacheEnabled()) return
		val size = calculateOptimalExpectedSize()
		instances = ObjectOpenHashSet<RecipeInput>(size)
	}

	@ApiStatus.Internal
	fun disableCache() {
		if(!isCacheEnabled()) return
		val size = instances!!.size
		instances = null

		if(size in MINIMUM_CACHE_SIZE..<MAXIMUM_CACHE_SIZE) {
			val tagCompound: NBTTagCompound = PersistentData.getTag()
			if(getExpectedInstanceAmount(tagCompound) !== size) {
				tagCompound.setInteger(DATA_NAME, size)
				PersistentData.save()
			}
		}
	}

	/**
	 * Tries to deduplicate the instance with previously cached instances. If there is no identical RecipeInput
	 * present in cache, the `recipeInput` will be put into cache, marked as cached, and returned subsequently.
	 *
	 * This operation returns `recipeInput` without doing anything if cache is disabled.
	 *
	 * @param recipeInput ingredient instance to be deduplicated
	 * @return Either previously cached instance, or `recipeInput` marked cached; or unmodified `recipeInput` instance if the cache is disabled
	 */
	fun deduplicate(recipeInput: RecipeInput): RecipeInput? {
		if(!isCacheEnabled() || recipeInput.cached) {
			return recipeInput
		}
		val cached: RecipeInput? = instances!!.addOrGet(recipeInput)
		if(cached === recipeInput) { // If recipeInput is cached just now...
			cached.setCached()
		}
		return cached
	}

	/**
	 * Tries to deduplicate each instance in the list with previously cached instances. If there is no identical
	 * RecipeInput present in cache, the `recipeInput` will be put into cache, marked as cached, and returned
	 * subsequently.
	 *
	 * This operation returns `inputs` without doing anything if cache is disabled.
	 *
	 * @param inputs list of ingredient instances to be deduplicated
	 * @return List of deduplicated instances, or `inputs` if the cache is disabled
	 */
	fun deduplicateInputs(inputs: MutableList<RecipeInput?>): MutableList<RecipeInput?> {
		if(!isCacheEnabled()) return inputs
		if(inputs.isEmpty()) return Collections.emptyList()
		val list: MutableList<RecipeInput?> = ArrayList(inputs.size)
		inputs.forEach { list.add(deduplicate(it!!)) }
		return list
	}

	/**
	 * Calculates the optimal expected size for the input cache:
	 *  1. Pick a Load Factor to test: i.e. `0.75f` (default).
	 *  2. Pick a Size to test: i.e. `8192`.
	 *  3. Internal array's size: next highest power of 2 for `size / loadFactor`, `nextHighestPowerOf2(8192 / 0.75) = 16384`.
	 *  4. The maximum amount of stored values before a rehash is required `arraySize * loadFactor`, `16384 * 0.75 = 12288`.
	 *  5. Compare with the known amount of values stored: `12288 >= 11774`.
	 *  6. If larger or equal, the initial capacity and load factor will not induce a rehash/resize.
	 *
	 * @return the optimal expected input cache size
	 */
	private fun calculateOptimalExpectedSize(): Int {
		val min = max(getExpectedInstanceAmount(PersistentData.getTag()), MINIMUM_CACHE_SIZE)
		for(i in 13..30) {
			val sizeToTest = 1 shl i
			val arraySize = nextHighestPowerOf2((sizeToTest / Hash.DEFAULT_LOAD_FACTOR).toInt())
			val maxStoredBeforeRehash = (arraySize * Hash.DEFAULT_LOAD_FACTOR).toInt()

			if(maxStoredBeforeRehash >= min) {
				return sizeToTest
			}
		}
		return MINIMUM_CACHE_SIZE
	}

	/**
	 * [Algorithm source.](https://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2)
	 *
	 * @param x the number to use
	 * @return the next highest power of 2 relative to the number
	 */
	private fun nextHighestPowerOf2(x: Int): Int {
		var x = x
		x--
		x = x or (x shr 1)
		x = x or (x shr 2)
		x = x or (x shr 4)
		x = x or (x shr 8)
		x = x or (x shr 16)
		x++
		return x
	}
}
