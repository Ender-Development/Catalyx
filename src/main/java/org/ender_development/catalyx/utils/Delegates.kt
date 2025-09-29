package org.ender_development.catalyx.utils

import net.minecraftforge.fml.common.Loader
import org.ender_development.catalyx.utils.extensions.loaded
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author scream at roz
 */
object Delegates {
	// is this needed? no, not really
	// was this fun to write? yes
	/**
	 * Only allow access to a property if a certain mod is loaded
	 *
	 * If the specified mod is not loaded, every attempt at writing to the property will do nothing, every attempt at reading the property will throw an [IllegalStateException]
	 */
	fun <V : Any> onlyIfLoaded(mod: String): ReadWriteProperty<Any?, V> =
		if(mod.loaded())
			OnlyIfTrue()
		else
			OnlyIfFalse(mod)

	private class OnlyIfFalse<V : Any>(val modName: String) : ReadWriteProperty<Any?, V> {
		override fun getValue(thisRef: Any?, property: KProperty<*>): V {
			throw IllegalStateException("Tried to get property '${property.name}' without mod '$modName' being loaded")
		}

		override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {}
	}

	private class OnlyIfTrue<V : Any>() : ReadWriteProperty<Any?, V> {
		var value: V? = null

		override fun getValue(thisRef: Any?, property: KProperty<*>): V {
			return value ?: throw IllegalStateException("Tried to get property '${property.name}' before initializing it")
		}

		override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
			this.value = value
		}
	}

	fun <V : Any> lazyProperty(getter: () -> V): ReadOnlyProperty<Any?, V> =
		object : ReadOnlyProperty<Any?, V> {
			var gotProperty = false
			lateinit var property: V

			override fun getValue(thisRef: Any?, property: KProperty<*>) =
				if(gotProperty)
					this.property
				else {
					gotProperty = true
					this.property = getter()
					this.property
				}
		}
}
