package org.ender_development.catalyx_.modules.coremodule

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.ender_development.catalyx.Reference
import org.ender_development.catalyx_.core.module.ModuleManager

@Config(modid = Reference.MODID, name = "${Reference.MODID}/${ModuleManager.MODULE_CFG_CATEGORY_NAME}", category = ModuleManager.MODULE_CFG_CATEGORY_NAME)
internal object CatalyxConfig {
	@Mod.EventBusSubscriber(modid = Reference.MODID)
	object ConfigEventHandler {
		@SubscribeEvent
		@JvmStatic
		fun onConfigChangedEvent(event: ConfigChangedEvent.OnConfigChangedEvent) {
			if(event.modID == Reference.MODID)
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE)
		}
	}
}
