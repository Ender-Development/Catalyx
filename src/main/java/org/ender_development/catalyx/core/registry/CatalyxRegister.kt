package org.ender_development.catalyx.core.registry

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.core.IProvider
import org.ender_development.catalyx.utils.extensions.modLoaded

class CatalyxRegister<V : IProvider<*>> : HashMap<ResourceLocation, Pair<V, Boolean>>() {
	fun add(provider: V): Boolean =
		provider.instance.registryName?.let {
			this[it] = provider to (provider.isEnabled && provider.modDependencies.evaluateModDependencies())
			true
		} ?: false

	val enabled: List<V>
		get() = values.filter { it.second }.map { it.first }

	val plural
		get() = if(enabled.size == 1) "" else "s"

	/**
	 * Special expansion to evaluate modDependencies strings.
	 */
	fun String.evaluateModDependencies(): Boolean =
		isEmpty() || split(',', ';').all {
			if(it[0] == '!')
				!it.substring(1).modLoaded()
			else
				it.modLoaded()
		}
}
