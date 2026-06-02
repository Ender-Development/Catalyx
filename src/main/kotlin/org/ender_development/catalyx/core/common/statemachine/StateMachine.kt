package org.ender_development.catalyx.core.common.statemachine

import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.core.utils.EnvironmentUtils

// S, E - State, Event

typealias StateTransition<S, E> = (state: S, event: E) -> S?
typealias StateAction<S> = (state: S) -> Unit

class StateMachine<S : Any, E : Any>(initialState: S) {
	private val transitions = mutableMapOf<Pair<S, E>, StateTransition<S, E>>()
	private val onEnterActions = mutableMapOf<S, MutableList<StateAction<S>>>()
	private val onExitActions = mutableMapOf<S, MutableList<StateAction<S>>>()

	var currentState = initialState
		private set

	fun transition(from: S, on: E, to: S) {
		transitions[from to on] = { _, _ -> to }
	}

	fun transition(from: S, on: E, handler: StateTransition<S, E>) {
		transitions[from to on] = handler
	}

	fun onEnter(state: S, action: StateAction<S>) {
		onEnterActions.getOrPut(state, ::mutableListOf).add(action)
	}

	fun onExit(state: S, action: StateAction<S>) {
		onExitActions.getOrPut(state, ::mutableListOf).add(action)
	}

	fun sendEvent(event: E): Boolean {
		val transition = transitions[currentState to event]
		val newState = transition?.invoke(currentState, event)

		if(newState == null || newState == currentState)
			return false

		// Exit current state
		onExitActions[currentState]?.forEach { it(currentState) }

		val previousState = currentState
		currentState = newState

		// Enter new state
		onEnterActions[currentState]?.forEach { it(currentState) }

		if(EnvironmentUtils.isDeobfuscated)
			Catalyx.LOGGER.debug("State Machine Transition: {} by {} -> {}", previousState, event, currentState)

		return true
	}
}
