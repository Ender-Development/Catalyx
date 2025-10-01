package org.ender_development.catalyx.core.registry

import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.core.IProvider
import org.ender_development.catalyx.utils.extensions.evaluate

class CatalyxRegister<V : IProvider<*>> : HashMap<ResourceLocation, Pair<V, Boolean>>() {
	fun add(provider: V): Boolean =
		provider.instance.registryName?.let {
			this[it] = Pair(provider, provider.isEnabled && provider.modDependencies.evaluate())
			true
		} ?: false

	val enabled: List<V>
		get() = this.values.filter { it.second }.map { it.first }

	val plural
		get() = if(enabled.size == 1) "" else "s"
}
