package org.ender_development.catalyx.integration.groovyscript

import com.cleanroommc.groovyscript.api.GroovyPlugin
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer
import net.minecraftforge.fml.common.Optional
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx.integration.IntegrationSubmodule
import org.ender_development.catalyx.integration.Mods
import org.ender_development.catalyx.modules.CatalyxModule
import org.ender_development.catalyx.modules.CatalyxModules

@Optional.Interface(modid = Mods.GROOVYSCRIPT, iface = "com.cleanroommc.groovyscript.api.GroovyPlugin", striprefs = true)
@CatalyxModule(moduleID = CatalyxModules.MODULE_GRS, containerID = Reference.MODID, modDependencies = [Mods.GROOVYSCRIPT], name = "Catalyx GroovyScript Integration Module", description = "Adds integration with GroovyScript")
class ModuleGroovyScript: IntegrationSubmodule(), GroovyPlugin {
	companion object {
		private lateinit var modSupportContainer: GroovyContainer<*>
	}

	@Optional.Method(modid = Mods.GROOVYSCRIPT)
	override fun onCompatLoaded(container: GroovyContainer<*>?) {
		modSupportContainer = container!!
	}

	override fun getModId() = Reference.MODID

	override fun getContainerName() = Reference.MOD_NAME

}
