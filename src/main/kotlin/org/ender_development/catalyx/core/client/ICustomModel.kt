package org.ender_development.catalyx.core.client

import com.google.common.collect.ImmutableMap
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.Catalyx
import org.ender_development.catalyx.api.v1.registry.IItemProvider
import org.ender_development.catalyx.core.client.sprite.DefaultSprite

interface ICustomModel {
	@SideOnly(Side.CLIENT)
	fun onBakeModel(event: ModelBakeEvent)

	@SideOnly(Side.CLIENT)
	fun onTextureStitch(event: TextureStitchEvent.Pre)
}

interface IAutoModel : ICustomModel, IItemProvider {
	@SideOnly(Side.CLIENT)
	override fun onTextureStitch(event: TextureStitchEvent.Pre) {
		event.map.setTextureEntry(DefaultSprite(textureLocation))
	}

	@SideOnly(Side.CLIENT)
	override fun onBakeModel(event: ModelBakeEvent) {
		try {
			val baseModel = ModelLoaderRegistry.getModel(modelParent)
			val retexturedModel = baseModel.retexture(ImmutableMap.of("layer0", textureLocation.toString()))
			val bakedModel = retexturedModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter())
			val bakedModelLoc = ModelResourceLocation(instance.delegate.name(), "inventory")
			event.modelRegistry.putObject(bakedModelLoc, bakedModel)
		} catch(e: Throwable) {
			Catalyx.LOGGER.catching(e)
		}
	}
}
