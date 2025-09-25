package org.ender_development.catalyx.modules

import net.minecraftforge.fml.common.eventhandler.Event

/**
 * Event fired when module containers should register their modules.
 * Fired on the MinecraftForge.EVENT_BUS in the [org.ender_development.catalyx.Catalyx.construction] method.
 */
class ModuleContainerRegistryEvent: Event() {}
