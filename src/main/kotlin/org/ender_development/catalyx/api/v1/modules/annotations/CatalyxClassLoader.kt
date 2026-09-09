package org.ender_development.catalyx.api.v1.modules.annotations

import net.minecraftforge.fml.common.LoaderState
import org.ender_development.catalyx.core.Reference

/**
 * If you really just want the JVM to load a class, when a [net.minecraftforge.fml.common.Mod] reaches a specific [LoaderState.ModState].
 *
 * @param modId A [String] representing a modid. Default: [Reference].MODID
 * @param modStage A [LoaderState.ModState] defining, when to load the class. Default: [LoaderState.ModState.INITIALIZED]
 */
annotation class CatalyxLoadClass(val modId: String = Reference.MODID, val modStage: LoaderState.ModState = LoaderState.ModState.INITIALIZED)
