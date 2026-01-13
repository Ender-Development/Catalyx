package org.ender_development.catalyx.api.v1.interfaces.cast

/**
 * Implement this to provide capability to try cast into [T]
 * no matter if you actually inhere from [T]
 *
 * This allows dynamic casting.
 *
 * @param T Type you can maybe cast into
 *
 * @see [ICanCastInto]
 */
interface ICanMaybeCastInto<T> {
	/**
	 * Allows to cast safely into [T]
	 *
	 * @return Equivalent of [T] for this or null if fails
	 */
	@Suppress("UNCHECKED_CAST")
	fun saveCastorNull(): T? = try {
		this as T
	} catch(e: ClassCastException) {
		null
	}
}
