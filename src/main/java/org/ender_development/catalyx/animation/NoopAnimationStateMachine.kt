package org.ender_development.catalyx.animation

import com.google.common.collect.ImmutableMap
import org.ender_development.catalyx.utils.SideUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.animation.ITimeValue
import net.minecraftforge.common.model.animation.IAnimationStateMachine

class NoopAnimationStateMachine() : IAnimationStateMachine {
	companion object {
		fun loadASM(location: ResourceLocation, customParameters: Map<String, ITimeValue>) =
			if(SideUtils.isDedicatedServer)
				NoopAnimationStateMachine()
			else
				ModelLoaderRegistry.loadASM(location, ImmutableMap.copyOf(customParameters))
	}

	override fun apply(time: Float) = null
	override fun shouldHandleSpecialEvents(value: Boolean) {}

	private var state: String? = null
	override fun transition(newState: String?) {
		state = newState
	}
	override fun currentState() = state
}
