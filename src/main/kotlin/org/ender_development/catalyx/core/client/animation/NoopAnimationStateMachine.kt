package org.ender_development.catalyx.core.client.animation

import com.google.common.collect.ImmutableMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import org.ender_development.catalyx.api.v1.utils.Utils

/**
 * No-op implementation of [IAnimationStateMachine].
 * Use [NoopAnimationStateMachine.loadASM] to only load ASM on the client-side.
 * Server-side gets No-op one instead.
 */
class NoopAnimationStateMachine() : IAnimationStateMachine {
	companion object {
		fun loadASM(location: ResourceLocation, customParameters: Map<String, ITimeValue>): IAnimationStateMachine =
			if(Utils.environment.isDedicatedServer)
				NoopAnimationStateMachine()
			else
				ModelLoaderRegistry.loadASM(location, ImmutableMap.copyOf(customParameters))
	}

	override fun shouldHandleSpecialEvents(value: Boolean) {}
	override fun apply(time: Float) =
		null

	private var state: String? = null

	override fun transition(newState: String?) {
		state = newState
	}

	override fun currentState() =
		state
}
