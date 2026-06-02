package org.ender_development.catalyx.core.common.statemachine

import kotlin.random.Random

class StateMachineDSL<S : Any, E : Any>(initialState: S) {
	private val machine = StateMachine<S, E>(initialState)

	fun state(state: S, block: StateBuilder<S, E>.() -> Unit) {
		StateBuilder(state, machine).block()
	}

	fun build() =
		machine
}

class StateBuilder<S : Any, E : Any>(private val state: S, private val machine: StateMachine<S, E>) {
	fun on(event: E, block: TransitionBuilder<S, E>.() -> Unit) {
		TransitionBuilder(state, event, machine).block()
	}

	fun onEnter(action: StateAction<S>) =
		machine.onEnter(state, action)

	fun onExit(action: StateAction<S>) =
		machine.onExit(state, action)
}

class TransitionBuilder<S : Any, E : Any>(private val from: S, private val event: E, private val machine: StateMachine<S, E>) {
	fun goto(to: S) =
		machine.transition(from, event, to)

	fun gotoIf(to: S, condition: (S, E) -> Boolean) =
		machine.transition(from, event) { state, event ->
			if(condition(state, event)) to else null
		}
}

fun <S : Any, E : Any> stateMachine(initialState: S, block: StateMachineDSL<S, E>.() -> Unit) =
	StateMachineDSL<S, E>(initialState).apply(block).build()

// TODO this was just for testing this DSL and seeing if it makes sense
@Suppress("unused")
private fun test() {
	val machine = stateMachine("initial") {
		state("initial") {
			on("start") {
				goto("running")
			}
		}
		state("running") {
			on("stop") {
				goto("initial")
			}
			on("next") {
				gotoIf("end") { state, event -> Random.nextDouble() < .5 }
			}
		}
		state("end") {
			onEnter {
				// goto("initial") // nope
				// sendEvent() // nope
			}
		}
	}
}
