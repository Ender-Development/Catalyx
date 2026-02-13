package org.ender_development.catalyx.core.common.statemachine

class StateMachineDSL<S : Any, E : Any>(initialState: S) {
    private val machine = StateMachine<S, E>(initialState)

    fun state(state: S, block: StateBuilder<S, E>.() -> Unit) {
        val builder = StateBuilder(state, machine)
        builder.block()
    }

    fun build(): StateMachine<S, E> =
        machine
}

class StateBuilder<S : Any, E : Any>(private val state: S, private val machine: StateMachine<S, E>) {

    fun on(event: E, block: TransitionBuilder<S, E>.() -> Unit) {
        val builder = TransitionBuilder(state, event, machine)
        builder.block()
    }

    fun onEnter(action: StateAction<S>) =
        machine.onEnter(state, action)

    fun onExit(action: StateAction<S>) =
        machine.onExit(state, action)
}

class TransitionBuilder<S : Any, E : Any>(private val from: S, private val event: E, private val machine: StateMachine<S, E>) {

    fun goto(to: S) =
        machine.transition(from, event, to)

    fun gotoIf(condition: (S, E) -> Boolean, to: S) =
        machine.transition(from, event) { s, e ->
            if (condition(s, e)) to else null
        }
}

fun <S : Any, E : Any> stateMachine(initialState: S, block: StateMachineDSL<S, E>.() -> Unit): StateMachine<S, E> {
    val dsl = StateMachineDSL<S, E>(initialState)
    dsl.block()
    return dsl.build()
}
