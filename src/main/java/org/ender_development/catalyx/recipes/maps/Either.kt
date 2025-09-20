package org.ender_development.catalyx.recipes.maps

import java.util.*
import java.util.function.Consumer

abstract class Either<L, R> private constructor() {
	companion object {
		fun <L, R> left(value: L): Either<L, R> =
			Left(value)

		fun <L, R> right(value: R): Either<L, R> =
			Right(value)
	}

	abstract fun <C, D> mapBoth(f1: (L) -> C, f2: (R) -> D): Either<C, D>

	abstract fun <T> map(l: (L) -> T, r: (R) -> T): T

	abstract fun ifLeft(consumer: Consumer<in L>): Either<L, R>

	abstract fun ifRight(consumer: Consumer<in R>): Either<L, R>

	abstract fun left(): L?

	abstract fun right(): R?

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
		override fun <C, D> mapBoth(f1: (L) -> C, f2: (R) -> D): Either<C, D> =
			Left(f1(value))

		override fun <T> map(l: (L) -> T, r: (R) -> T): T =
			l(value)

		override fun ifLeft(consumer: Consumer<in L>): Either<L, R> {
			consumer.accept(value)
			return this
		}

		override fun ifRight(consumer: Consumer<in R>): Either<L, R> =
			this

		override fun left(): L? =
			value

		override fun right(): R? =
			null

		override fun toString(): String =
			"Left[$value]"

		override fun equals(other: Any?): Boolean {
			if(this === other) return true
			if(other == null || javaClass != other.javaClass) return false
			val left: Left<*, *> = other as Left<*, *>
			return Objects.equals(value, left.value)
		}

		override fun hashCode(): Int =
			Objects.hash(value)
	}

	private class Right<L, R>(val value: R) : Either<L, R>() {
		override fun <C, D> mapBoth(f1: (L) -> C, f2: (R) -> D): Either<C, D> =
			Right(f2(value))

		override fun <T> map(l: (L) -> T, r: (R) -> T): T =
			r(value)

		override fun ifLeft(consumer: Consumer<in L>): Either<L, R> =
			this

		override fun ifRight(consumer: Consumer<in R>): Either<L, R> {
			consumer.accept(value)
			return this
		}

		override fun left(): L? =
			null

		override fun right(): R? =
			value

		override fun toString(): String {
			return "Right[$value]"
		}

		override fun equals(other: Any?): Boolean {
			if(this === other) return true
			if(other == null || javaClass != other.javaClass) return false
			val right: Right<*, *> = other as Right<*, *>
			return Objects.equals(value, right.value)
		}

		override fun hashCode(): Int {
			return Objects.hash(value)
		}
	}
}
