package org.ender_development.catalyx.core

import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.connect.IMixinConnector

/**
 * Since MixinBooter v11.x we no longer need a core mod to load mixins.
 * Instead, we need to point our buildscript to the connector class inside the manifest.
 * @see zone.rong.mixinbooter.service.ModDiscoverer.isModPresent
 */
@Suppress("UNUSED")
class CatalyxConnector: IMixinConnector {
	override fun connect() {
		Mixins.addConfiguration("mixins.catalyx.modules.json")
	}
}
