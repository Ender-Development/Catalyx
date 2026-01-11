package org.ender_development.catalyx.animation

import com.google.common.collect.ImmutableMap
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine
import org.ender_development.catalyx_.core.utils.SideUtils

class NoopAnimationStateMachine() : IAnimationStateMachine {
	companion object {
		fun loadASM(location: ResourceLocation, customParameters: Map<String, ITimeValue>): IAnimationStateMachine =
			if(SideUtils.isDedicatedServer)
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
