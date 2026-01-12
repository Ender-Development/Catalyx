package org.ender_development.catalyx.core

import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import zone.rong.mixinbooter.IEarlyMixinLoader

@IFMLLoadingPlugin.Name("CatalyxCoreMod")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(1)
class CatalyxCoreMod : IFMLLoadingPlugin, IEarlyMixinLoader {
	override fun getASMTransformerClass() =
		emptyArray<String>()

	override fun getModContainerClass() =
		null

	override fun getSetupClass() =
		null

	override fun injectData(data: Map<String?, Any?>?) {}

	override fun getAccessTransformerClass() =
		null

	override fun getMixinConfigs() =
		listOf("mixins/mixins.catalyx.modules.json")
}
