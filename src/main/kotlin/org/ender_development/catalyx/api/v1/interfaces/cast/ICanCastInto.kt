package org.ender_development.catalyx.api.v1.interfaces.cast

/**
 * Implement this to provide capability to cast into [T]
 * no matter if you actually inhere from [T]
 *
 * This allows dynamic casting.
 *
 * @param T Type you can cast into
 *
 * @see [ICanMaybeCastInto]
 */
interface ICanCastInto<T> : ICanMaybeCastInto<T> {

	/**
	 * Allows to cast safely into [T]
	 *
	 * The default implementation shouldn't be used because it's actually not really save.
	 * ONLY USE THE DEFAULT IMPLEMENTATION IF YOU INHERE FROM [T]!
	 *
	 * @return Equivalent of [T] for this
	 */
	@Suppress("UNCHECKED_CAST")
	fun saveCast(): T = try {
		this as T
	} catch(e: ClassCastException) {
		throw IllegalStateException(
			"This interface should not be implemented if you cant cast safely into T " +
					"Override this to do a manuel dynamic cast.",
			e
		)
	}
}
