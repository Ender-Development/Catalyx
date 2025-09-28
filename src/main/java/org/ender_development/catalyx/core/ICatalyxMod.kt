package org.ender_development.catalyx.core

import org.ender_development.catalyx.Reference

interface ICatalyxMod {
	 companion object {
		 /**
		  * The dependencies string for the Catalyx mod. Includes:
		  * - [Forgelin Continuous](https://github.com/ChAoSUnItY/Forgelin-Continuous) (for Kotlin support) - required
		  * - [GroovyScript](https://github.com/CleanroomMC/GroovyScript) (for GroovyScript support) - optional
		  */
		 const val DEPENDENCIES = "required-after:forgelin_continuous@[${Reference.KOTLIN_VERSION},);after:groovyscript@[${Reference.GROOVYSCRIPT_VERSION},);"

		 /**
		  * The full dependencies string to add to your `@Mod` annotation to depend on Catalyx.
		  * This includes the Catalyx mod itself, as well as all of its dependencies.
		  */
		 const val CATALYX_ADDON = "required-after:${Reference.MODID}@[${Reference.VERSION},);$DEPENDENCIES"

		 /**
		  * The mod language adapter to use for Kotlin support.
		  * This requires the [Forgelin Continuous](https://github.com/ChAoSUnItY/Forgelin-Continuous) mod to be installed.
		  */
		 const val MOD_LANGUAGE_ADAPTER = "io.github.chaosunity.forgelin.KotlinAdapter"
	 }

	val modSettings: CatalyxSettings
}
