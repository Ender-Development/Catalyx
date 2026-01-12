package org.ender_development.catalyx.modules.integration.groovyscript

import com.cleanroommc.groovyscript.GroovyScript
import com.cleanroommc.groovyscript.api.GroovyPlugin
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer
import net.minecraftforge.fml.common.Optional
import org.ender_development.catalyx.core.Reference
import org.ender_development.catalyx.modules.integration.IntegrationModule
import org.ender_development.catalyx.core.utils.Mods
import org.ender_development.catalyx.core.module.CatalyxModule
import org.ender_development.catalyx.core.module.ModuleManager
import org.ender_development.catalyx.modules.CatalyxInternalModuleContainer
import org.ender_development.catalyx.core.utils.extensions.subLogger

@Optional.Interface(modid = Mods.GROOVYSCRIPT, iface = "com.cleanroommc.groovyscript.api.GroovyPlugin", striprefs = true)
@CatalyxModule(
	moduleId = CatalyxInternalModuleContainer.MODULE_GRS,
	containerId = Reference.MODID,
	modDependencies = [Mods.GROOVYSCRIPT],
	name = "Catalyx GroovyScript Integration Module",
	description = "Adds integration with GroovyScript",
	moduleDependencies = ["${Reference.MODID}:${CatalyxInternalModuleContainer.MODULE_INTEGRATION}"]
)
internal class ModuleGroovyScript : IntegrationModule(), GroovyPlugin {
	override val logger = super.logger.subLogger("GroovyScript")

	companion object {
		private lateinit var modSupportContainer: GroovyContainer<*>

		val isRunning = ModuleManager.isModuleEnabled(CatalyxInternalModuleContainer.MODULE_GRS) && GroovyScript.getSandbox().isRunning
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
