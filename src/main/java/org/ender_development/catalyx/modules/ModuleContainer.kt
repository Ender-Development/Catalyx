package org.ender_development.catalyx.modules

/**
 * Annotate your [IModuleContainer] with this for it to be automatically registered.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleContainer()
