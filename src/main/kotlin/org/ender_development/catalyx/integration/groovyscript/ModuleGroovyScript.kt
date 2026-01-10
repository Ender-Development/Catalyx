package org.ender_development.catalyx.integration.groovyscript

import com.cleanroommc.groovyscript.GroovyScript
import com.cleanroommc.groovyscript.api.GroovyPlugin
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer
import net.minecraftforge.fml.common.Optional
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.IntegrationSubmodule
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.ModuleManager
import org.ender_development.catalyx.modules.catalyx.CatalyxModules
import org.ender_development.catalyx.utils.extensions.subLogger

@Optional.Interface(modid = Mods.GROOVYSCRIPT, iface = "com.cleanroommc.groovyscript.api.GroovyPlugin", striprefs = true)
@CatalyxModule(
	moduleId = CatalyxModules.MODULE_GRS,
	containerId = Reference.MODID,
	modDependencies = [Mods.GROOVYSCRIPT],
	name = "Catalyx GroovyScript Integration Module",
	description = "Adds integration with GroovyScript"
)
internal class ModuleGroovyScript : IntegrationSubmodule(), GroovyPlugin {
	override val logger = super.logger.subLogger("GroovyScript")

	companion object {
		private lateinit var modSupportContainer: GroovyContainer<*>

		val isRunning = ModuleManager.isModuleEnabled(CatalyxModules.MODULE_GRS) && GroovyScript.getSandbox().isRunning
	}

	@Optional.Method(modid = Mods.GROOVYSCRIPT)
	override fun onCompatLoaded(container: GroovyContainer<*>?) {
		modSupportContainer = container!!
	}

	override fun getModId() =
		Reference.MODID

	override fun getContainerName() =
		Reference.MOD_NAME
}
