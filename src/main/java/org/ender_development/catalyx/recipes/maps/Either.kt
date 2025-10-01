package org.ender_development.catalyx.recipes.maps

abstract class Either<L, R> private constructor() {
	companion object {
		fun <L, R> left(value: L): Either<L, R> =
			Left(value)

		fun <L, R> right(value: R): Either<L, R> =
			Right(value)
	}

	abstract fun <C, D> mapBoth(l: (L) -> C, r: (R) -> D): Either<C, D>

	abstract fun <T> map(l: (L) -> T, r: (R) -> T): T

	abstract fun ifLeft(consumer: (left: L) -> Unit): Either<L, R>

	abstract fun ifRight(consumer: (right: R) -> Unit): Either<L, R>

	abstract val left: L?

	abstract val right: R?

	fun <T> mapLeft(l: (L) -> T): Either<T, R> =
		map({ left(l(it)) }, { right(it) })

	fun <T> mapRight(l: (R) -> T): Either<L, T> =
		map({ left(it) }, { right(l(it)) })

	fun orThrow(): L {
		return map({ it }, { r ->
			when(r) {
				is Throwable -> throw RuntimeException(r)
				else -> throw RuntimeException(r.toString())
			}
		})
	}

	fun swap(): Either<R, L> =
		map({ right(it) }, { left(it) })

	fun <L2> flatMap(function: (L) -> Either<L2, R>): Either<L2, R> =
		map(function) { right(it) }

	private class Left<L, R>(val value: L) : Either<L, R>() {
		override fun <C, D> mapBoth(l: (L) -> C, r: (R) -> D): Either<C, D> =
			Left(l(value))

		override fun <T> map(l: (L) -> T, r: (R) -> T): T =
			l(value)

		override fun ifLeft(consumer: (left: L) -> Unit): Either<L, R> {
			consumer(value)
			return this
		}

		override fun ifRight(consumer: (right: R) -> Unit): Either<L, R> =
			this

		override val left: L? = value

		override val right: R? = null

		override fun toString() =
			"Either.Left[$value]"

		override fun equals(other: Any?) =
			this === other || (other is Left<*, *> && value == other.value)

		override fun hashCode() =
			value.hashCode()
	}

	private class Right<L, R>(val value: R) : Either<L, R>() {
		override fun <C, D> mapBoth(l: (L) -> C, r: (R) -> D): Either<C, D> =
			Right(r(value))

		override fun <T> map(l: (L) -> T, r: (R) -> T): T =
			r(value)

		override fun ifLeft(consumer: (left: L) -> Unit): Either<L, R> =
			this

		override fun ifRight(consumer: (right: R) -> Unit): Either<L, R> {
			consumer(value)
			return this
		}

		override val left: L? = null

		override val right: R? = value

		override fun toString() =
			"Either.Right[$value]"

		override fun equals(other: Any?) =
			this === other || (other is Right<*, *> && value == other.value)

		override fun hashCode() =
			value.hashCode()
	}
}
